package dispatcher.controller;

import dispatcher.configuration.RabbitConfig;
import dispatcher.service.UpdateProducer;
import dispatcher.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;


@RequiredArgsConstructor
@Log4j
@Component
public class UpdateProcessor {
    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    private final RabbitConfig rabbitConfig;

    public void registerBot( TelegramBot telegramBot ) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate( Update update ) {
        if ( update == null ) {
            log.error( "Received update is null!" );
            return;
        }

        if ( update.hasMessage() ) {
            distributeMessageByType( update );
        } else {
            log.error( "Unsupported message type: " + update );
        }
    }

    public void setView( SendMessage sendMessage ) {
        telegramBot.sendAnswerMessage( sendMessage );
    }

    private void distributeMessageByType( Update update ) {
        Message message = update.getMessage();

        // Map message types to corresponding processing methods
        Map<Predicate<Message>, Consumer<Update>> messageProcessor = Map.of(
                Message::hasText, this::processTextMessage,
                Message::hasDocument, this::processDocMessage,
                Message::hasPhoto, this::processPhotoMessage
        );

        // Process the message by finding the first matching processor
        messageProcessor.entrySet().stream()
                .filter( entry -> entry.getKey().test( message )) // Filter by message type condition
                // Get the first matching processor
                .findFirst()
                .ifPresentOrElse(
                        entry -> entry.getValue().accept( update ), // If found, call the corresponding method
                        () -> setUnsupportedMessageType( update ) // If no match, handle unsupported message type
                );
    }

    private void setUnsupportedMessageType( Update update ) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText( update, "Unsupported message type!" );
        setView( sendMessage );
    }

    private void processPhotoMessage( Update update ) {
        updateProducer.produce( rabbitConfig.getPhotoMessageUpdateQueue(), update );
        setFileIsReceivedView( update );
    }

    private void processDocMessage( Update update ) {
        updateProducer.produce( rabbitConfig.getDocMessageUpdateQueue(), update );
    }

    private void processTextMessage( Update update ) {
        updateProducer.produce( rabbitConfig.getTextMessageUpdateQueue(), update );
    }

    private void setFileIsReceivedView( Update update ) {
        SendMessage sendMessage =
                messageUtils.generateSendMessageWithText( update, "File is being processed." );
        setView( sendMessage );
    }
}

