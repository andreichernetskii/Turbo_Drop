package dispatcher.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Log4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramWebhookBot {

    @Value( "${bot.name}" )
    private String botName;

    @Value( "${bot.token}" )
    private String botToken;

    @Value( "${bot.uri}" )
    private String botUri;

    private final UpdateProcessor updateProcessor;

    @PostConstruct
    public void init() {
        updateProcessor.registerBot( this );

        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url( botUri )
                    .build();
            this.setWebhook( setWebhook );
        } catch ( TelegramApiException e ) {
            log.error( e );
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

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
    public String getBotPath() {
        return "/update";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived( Update update ) {
        return null;
    }
}
