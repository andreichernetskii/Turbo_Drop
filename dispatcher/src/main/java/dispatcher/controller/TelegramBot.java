package dispatcher.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value( "${bot.name}" )
    private String botName;
    @Value( "${bot.token}" )
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived( Update update ) {
        var originalMessage = update.getMessage();
        log.debug( originalMessage.getText() );

        var response = new SendMessage();
        response.setChatId( originalMessage.getChatId().toString() );
        response.setText( "Hello from Bot" );

        sendAnswerMessage( response );
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
}
