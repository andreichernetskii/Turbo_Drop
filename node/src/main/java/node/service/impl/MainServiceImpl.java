package node.service.impl;

import common_jpa.dao.AppUserDAO;
import common_jpa.entity.AppUser;
import common_jpa.entity.enums.UserState;
import node.dao.RawDataDAO;
import node.entity.RawData;
import node.service.MainService;
import node.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;

    public MainServiceImpl( RawDataDAO rawDataDAO,
                            ProducerService producerService,
                            AppUserDAO appUserDAO ) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage( Update update ) {
        saveRawData( update );

        Message textMessage = update.getMessage();
        User telegramUser = textMessage.getFrom();
        AppUser appUser = findOrSaveAppUser( telegramUser );

        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( message.getChatId().toString() );
        sendMessage.setText( "Hello from Node!" );

        producerService.producerAnswer( sendMessage );
    }

    public AppUser findOrSaveAppUser( User telegramUser ) {
        AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId( telegramUser.getId() );

        if ( persistentAppUser == null ) {
            AppUser transientUser = AppUser.builder()
                    .telegramUserId( telegramUser.getId() )
                    .userName( telegramUser.getUserName() )
                    .firstName( telegramUser.getFirstName() )
                    .lastName( telegramUser.getLastName() )
                    .isActive( true )
                    .state( UserState.BASIC_STATE )
                    .build();

            return appUserDAO.save( transientUser );
        }

        return persistentAppUser;
    }

    private void saveRawData( Update update ) {
        RawData rawData = RawData.builder()
                .event( update )
                .build();

        rawDataDAO.save( rawData );
    }
}
