package node.service.impl;

import common_jpa.dao.AppUserDAO;
import common_jpa.entity.AppDocument;
import common_jpa.entity.AppPhoto;
import common_jpa.entity.AppUser;
import common_jpa.entity.enums.UserState;
import lombok.extern.log4j.Log4j;
import node.dao.RawDataDAO;
import node.entity.RawData;
import node.exceptinos.UploadFileException;
import node.service.FileService;
import node.service.MainService;
import node.service.ProducerService;
import node.service.enums.ServiceCommands;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final AppUserDAO appUserDAO;
    private final ProducerService producerService;
    private final FileService fileService;

    public MainServiceImpl( RawDataDAO rawDataDAO,
                            ProducerService producerService,
                            AppUserDAO appUserDAO, FileService fileService ) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage( Update update ) {
        saveRawData( update );

        AppUser appUser = findOrSaveAppUser( update );
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output = "";
        ServiceCommands serviceCommand = ServiceCommands.fromValue( text );

        if ( ServiceCommands.CANCEL.equals( serviceCommand ) ) {
            output = cancelProcess( appUser );
        } else if ( UserState.BASIC_STATE.equals( userState ) ) {
            output = processServiceCommand( appUser, serviceCommand );
        } else if ( UserState.WAIT_FOR_EMAIL.equals( userState ) ) {
            // todo: add processing e-mails
        } else {
            log.error( "Unknown user state!" );
            output = "Unknown error! Provide command /cancel and try again.";
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer( output, chatId );
    }

    @Override
    public void processDocMessage( Update update ) {
        saveRawData( update );

        AppUser appUser = findOrSaveAppUser( update );
        Long chatId = update.getMessage().getChatId();

        if ( isNotAllowedToSendContent( chatId, appUser ) ) {
            return;
        }

        try {
            AppDocument document = fileService.processDoc( update.getMessage() );
            //todo: add link for downloading the document
            String answer = "Document successfully loaded! Link for downloading: test.test";
            sendAnswer( answer, chatId );
        } catch ( UploadFileException exception ) {
            log.error( exception );
            String error = "Loading failed. Try again later.";
            sendAnswer( error, chatId );
        }
    }

    @Override
    public void processPhotoMessage( Update update ) {
        saveRawData( update );

        AppUser appUser = findOrSaveAppUser( update );
        Long chatId = update.getMessage().getChatId();

        if ( isNotAllowedToSendContent( chatId, appUser ) ) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto( update.getMessage() );
            String answer = "Photo successfully loaded! Link for downloading: test.test";;
            sendAnswer( answer, chatId );
        } catch ( UploadFileException exception ) {
            log.error( exception );
            String error = "Loading failed. Try again later.";
            sendAnswer( error, chatId );
        }


    }

    private boolean isNotAllowedToSendContent( Long chatId, AppUser appUser ) {
        UserState userState = appUser.getState();

        if ( !appUser.getIsActive() ) {
            String error = "Pls register or activate your account for loading content.";
            sendAnswer( error, chatId );
            return true;
        } else if ( !UserState.BASIC_STATE.equals( userState ) ) {
            String error = "Cancel current command with /cancel for sending content.";
            sendAnswer( error, chatId );
            return true;
        }

        return false;
    }

    private void sendAnswer( String output, Long chatId ) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( chatId );
        sendMessage.setText( output );

        producerService.producerAnswer( sendMessage );
    }

    private String processServiceCommand( AppUser appUser, ServiceCommands cmd ) {
        if ( ServiceCommands.REGISTRATION.equals( cmd ) ) {
            //todo: add registration
            return "Temporary not available. Work in progress";
        } else if ( ServiceCommands.HELP.equals( cmd ) ) {
            return help();
        } else if ( ServiceCommands.START.equals( cmd ) ) {
            return "Greetings! Type /help for showing a list of commands.";
        } else {
            return "Unknown command!";
        }
    }

    private String help() {
        return "List of commands:\n" +
                "/cancel - cancel the current command;\n" +
                "/registration - user registration";
    }

    private String cancelProcess( AppUser appUser ) {
        appUser.setState( UserState.BASIC_STATE );
        appUserDAO.save( appUser );

        return "Command canceled!";
    }

    public AppUser findOrSaveAppUser( Update update ) {
        User telegramUser = update.getMessage().getFrom();
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
