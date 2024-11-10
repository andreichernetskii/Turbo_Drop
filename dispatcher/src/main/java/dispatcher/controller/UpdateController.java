package dispatcher.controller;

import dispatcher.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    public UpdateController( MessageUtils messageUtils ) {
        this.messageUtils = messageUtils;
    }

    public void registerBot( TelegramBot telegramBot ) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate( Update update ) {
        if ( update == null ) {
            log.error( "Received update is null!" );
            return;
        }

        if ( update.getMessage() != null ) {
            distributeMessageByType( update );
        } else {
            log.error( "Unsupported message type: " + update );
        }
    }

    private void distributeMessageByType( Update update ) {
        Message message = update.getMessage();

        // if-else variant
//        if ( message.getText() != null ) {
//            processTextMessage( update );
//        } else if ( message.getDocument() != null ) {
//            processDocMessage( update );
//        } else if ( message.getPhoto() != null ) {
//            processPhotoMessage( update );
//        } else {
//            setUnsupportedMessageType( update );
//        }

        // Map variant
        // Map message types to corresponding processing methods
        Map<Predicate<Message>, Consumer<Update>> messageProcessor = Map.of(
                msg -> msg.getText() != null, this::processTextMessage,
                msg -> msg.getDocument() != null, this::processDocMessage,
                msg -> msg.getPhoto( ) != null, this::processPhotoMessage
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

    private void setView( SendMessage sendMessage ) {
        telegramBot.sendAnswerMessage( sendMessage );
    }

    private void processPhotoMessage( Update update ) {

    }

    private void processDocMessage( Update update ) {

    }

    private void processTextMessage( Update update ) {

    }
}
