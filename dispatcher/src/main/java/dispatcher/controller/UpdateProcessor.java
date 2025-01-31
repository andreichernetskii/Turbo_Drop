package dispatcher.controller;

import dispatcher.configuration.RabbitConfig;
import dispatcher.service.UpdateProducer;
import dispatcher.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Controller for distributing and processing incoming messages from the Telegram bot.
 *
 * This class handles the routing of incoming updates, validates the data, and delegates
 * specific types of messages (text, documents, photos) to appropriate methods for further processing.
 */
@RequiredArgsConstructor
@Log4j
@Component
public class UpdateProcessor {

    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    private final RabbitConfig rabbitConfig;

    /**
     * Registers the bot instance for sending responses.
     * This is used to avoid a cyclical dependency between UpdateProcessor and TelegramBot.
     *
     * @param telegramBot - the bot instance used to send messages.
     */
    public void registerBot( TelegramBot telegramBot ) {
        this.telegramBot = telegramBot;
    }

    /**
     * Initializes and registers the bot instance with Telegram API.
     * This method is triggered when the application context is refreshed.
     *
     * It ensures that the bot is properly registered to handle incoming updates.
     * Any exceptions during the registration process are logged as errors.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void initBot() {

        try {

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);

        } catch (TelegramApiException e) {

            log.error(e);
        }
    }

    /**
     * Method for initial validation and processing of incoming data.
     * The method checks if the provided update contains a message and processes it accordingly.
     *
     * @param update - an object representing updates from Telegram chat,
     *                 such as a new message, status change, or chat command.
     *                 The Update class is part of the Telegram Bot API and
     *                 encapsulates various types of events received from Telegram servers.
     */
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

    /**
     * Sends a prepared message to the user through the bot.
     *
     * @param sendMessage - the message to be sent.
     */
    public void setView( SendMessage sendMessage ) {
        telegramBot.sendAnswerMessage( sendMessage );
    }

    /**
     * Routes the incoming message to the appropriate processing method
     * based on its type (text, document, or photo).
     *
     * @param update - the update containing the message to be processed.
     */
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
                // Filter by message type condition
                .filter( entry -> entry.getKey().test( message ))
                // Get the first matching processor
                .findFirst()
                .ifPresentOrElse(
                        // If found, call the corresponding method
                        entry -> entry.getValue().accept( update ),
                        // If no match, handle unsupported message type
                        () -> setUnsupportedMessageType( update )
                );
    }

    /**
     * Handles unsupported message types by informing the user.
     *
     * @param update - the update containing the unsupported message.
     */
    private void setUnsupportedMessageType( Update update ) {

        SendMessage sendMessage = messageUtils.generateSendMessageWithText( update, "Unsupported message type!" );
        setView( sendMessage );
    }

    /**
     * Processes photo messages by sending the update to the appropriate RabbitMQ queue.
     *
     * @param update - the update containing the photo message.
     */
    private void processPhotoMessage( Update update ) {

        updateProducer.produce( rabbitConfig.getPhotoMessageUpdateQueue(), update );
        setFileIsReceivedView( update );
    }

    /**
     * Processes document messages by sending the update to the appropriate RabbitMQ queue.
     *
     * @param update - the update containing the document message.
     */
    private void processDocMessage( Update update ) {

        updateProducer.produce( rabbitConfig.getDocMessageUpdateQueue(), update );
    }

    /**
     * Processes text messages by sending the update to the appropriate RabbitMQ queue.
     *
     * @param update - the update containing the text message.
     */
    private void processTextMessage( Update update ) {

        updateProducer.produce( rabbitConfig.getTextMessageUpdateQueue(), update );
    }

    /**
     * Sends a confirmation to the user that a file is being processed.
     *
     * @param update - the update containing the file message.
     */
    private void setFileIsReceivedView( Update update ) {

        SendMessage sendMessage =
                messageUtils.generateSendMessageWithText( update, "File is being processed." );
        setView( sendMessage );
    }
}

