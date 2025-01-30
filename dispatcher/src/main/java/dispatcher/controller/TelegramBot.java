package dispatcher.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Class for interaction with Telegram servers.
 *
 * This class extends the main interface of Telegram bots that operate via a webhook.
 * It is responsible for configuring the webhook, handling incoming updates, and sending responses.
 */
@Log4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value( "${bot.name}" )
    private String botName;

    @Value( "${bot.token}" )
    private String botToken;

    private final UpdateProcessor updateProcessor;

    /**
     * Initializes the bot by registering it with the UpdateProcessor.
     *
     * This method is annotated with {@code @PostConstruct}, ensuring it runs automatically
     * after the bot instance is created and dependencies are injected.
     */
    @PostConstruct
    public void init() { updateProcessor.registerBot( this ); }

    /**
     * Returns the bot's username.
     * This is used to identify the bot on Telegram servers.
     *
     * @return the username of the bot.
     */
    @Override
    public String getBotUsername() {
        return botName;
    }

    /**
     * Returns the bot's token.
     * This token is used to authenticate the bot with Telegram servers.
     *
     * @return the token of the bot.
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Sends a prepared message to a chat via the Telegram bot.
     *
     * @param sendMessage - the message to be sent, encapsulated in a {@link SendMessage} object.
     */
    public void sendAnswerMessage( SendMessage sendMessage ) {

        if ( sendMessage != null ) {
            try {
                execute( sendMessage );
            } catch ( TelegramApiException exception ) {
                log.error( exception );
            }
        }
    }

    @Override
    public void onUpdateReceived( Update update ) {
        updateProcessor.processUpdate( update );
    }
}
