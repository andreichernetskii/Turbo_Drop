package node.service.impl;

import common.dao.AppUserDAO;
import common.entity.AppDocument;
import common.entity.AppPhoto;
import common.entity.AppUser;
import common.entity.enums.UserActiveProcess;
import common.entity.enums.UserState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import node.dao.RawDataDAO;
import node.entity.RawData;
import node.exceptions.UploadFileException;
import node.service.AppUserService;
import node.service.FileService;
import node.service.MainService;
import node.service.ProducerService;
import node.service.enums.LinkType;
import node.service.enums.ServiceCommands;
import node.utils.Decoder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for handling messages from users in a Telegram bot.
 * Implements the logic for processing text messages, commands, document, and photo uploads.
 * Manages user states, registers users, and sends responses.
 */
@Log4j
@RequiredArgsConstructor
@Service
public class DefaultMainService implements MainService {

    private final RawDataDAO rawDataDAO;

    private final AppUserDAO appUserDAO;

    private final ProducerService producerService;

    private final FileService fileService;

    private final AppUserService appUserService;

    /**
     * Processes incoming text messages from users.
     * Based on the user's state and the command provided, it performs the appropriate actions:
     * user registration, providing help, or canceling the current process.
     *
     * @param update The update object from Telegram containing the message.
     */
    @Async
    @Transactional
    @Override
    public void processTextMessage( Update update ) {

        saveRawData( update );

        AppUser appUser = findOrSaveAppUser( update );
        String text = update.getMessage().getText();
        ServiceCommands serviceCommand = ServiceCommands.fromValue( text );
        StringBuilder output = new StringBuilder();

        if (ServiceCommands.CANCEL.equals(serviceCommand)) {
            output.append(cancelProcess(appUser));
        } else if (appUser.getUserActiveProcess().equals(UserActiveProcess.REGISTRATION_IN_PROCESS)) {
            output.append(appUserService.setEmail(appUser, text));
        } else {
            output.append(processServiceCommand(appUser, serviceCommand));
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer( output.toString(), chatId );
    }



    /**
     * Processes incoming document messages from users.
     * Checks if the user is allowed to send content and processes the document.
     * If successful, a download link is generated and sent to the user.
     *
     * @param updates The list of updates from Telegram containing the document message.
     */
    @Async
    @Override
    public void processDocMessage( List<Update> updates ) {

        saveRawData( updates );

        for (Update update : updates) {

            AppUser appUser = findOrSaveAppUser( update );
            Long chatId = update.getMessage().getChatId();

            if ( isNotAllowedToSendContent( chatId, appUser ) ) {

                log.debug("User " + chatId + " is not allowed to send content." );
                return;
            }

            try {

                AppDocument document = fileService.processDoc( update.getMessage() );
                String link = fileService.generateLink( document.getId(), LinkType.GET_DOC );
                String answer = "Document successfully loaded! Link for downloading: " + link;

                sendAnswer( answer, chatId );

            } catch ( UploadFileException exception ) {

                log.error( exception );
                String error = "Loading failed. Try again later.";

                sendAnswer( error, chatId );
            }
        }

    }

    /**
     * Processes incoming photo messages from users.
     * Checks if the user is allowed to send content and processes the photo.
     * If successful, a download link is generated and sent to the user.
     *
     * @param updates The list of updates from Telegram containing the photo message.
     */
    @Async
    @Override
    public void processPhotoMessage( List<Update> updates ) {

        saveRawData( updates );

        for (Update update : updates) {

            AppUser appUser = findOrSaveAppUser( update );
            Long chatId = update.getMessage().getChatId();

            if ( isNotAllowedToSendContent( chatId, appUser ) ) {
                return;
            }

            try {

                AppPhoto photo = fileService.processPhoto( update.getMessage() );
                String link = fileService.generateLink( photo.getId(), LinkType.GET_PHOTO );
                String answer = "Photo successfully loaded! Link for downloading: " + link;

                sendAnswer( answer, chatId );

            } catch ( UploadFileException exception ) {

                log.error( exception );
                String error = "Loading failed. Try again later.";

                sendAnswer( error, chatId );
            }
        }
    }

    @Async
    @Override
    public void activateUser(String encryptedUserId) {
        appUserService.activateUser(encryptedUserId);
    }

    /**
     * Checks if the user is allowed to send content.
     * The user must be active and in the basic state to send content.
     *
     * @param chatId The chat ID of the user.
     * @param appUser The user object.
     * @return true if the user is not allowed to send content, false otherwise.
     */
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

    /**
     * Sends a message to the specified chat.
     *
     * @param output The message content to be sent.
     * @param chatId The chat ID of the recipient.
     */
    private void sendAnswer( String output, Long chatId ) {

        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( chatId );
        sendMessage.setText( output );

        producerService.producerAnswer( sendMessage );
    }

    /**
     * Processes service commands like registration, help, etc.
     *
     * @param appUser The user object.
     * @param cmd The service command.
     * @return The response string to be sent to the user.
     */
    private String processServiceCommand( AppUser appUser, ServiceCommands cmd ) {

        StringBuilder strAnswer = new StringBuilder();

        if ( ServiceCommands.START.equals( cmd ) ) {
            strAnswer.append("Greetings! Type /help for showing a list of commands.");
            if (appUser.getState().equals(UserState.DEMO_STATE)) {
                strAnswer.append(
                        "\nYour current state is DEMO. You are able to send one document " +
                                "or photo with a maximum size of 5MB."
                );
            }
            return strAnswer.toString();
        } else if (ServiceCommands.REGISTRATION.equals(cmd)) {
            return appUserService.registerUser( appUser );
        } else if (ServiceCommands.HELP.equals( cmd )) {
            return help();
        } else {
            return strAnswer.append("Unknown command!").toString();
        }
    }

    /**
     * Provides a help message with the list of available commands.
     *
     * @return The help message.
     */
    private String help() {

        return "List of commands:\n" +
                "/cancel - cancel the current process;\n" +
                "/registration - user registration";
    }

    /**
     * Cancels the current process for the user and sets their active process to NONE.
     *
     * @param appUser The user object.
     * @return The confirmation message for the user.
     */
    private String cancelProcess( AppUser appUser ) {

        appUser.setUserActiveProcess(UserActiveProcess.NONE);
        appUserDAO.save( appUser );

        return "Command canceled!";
    }

    /**
     * Finds an existing user or creates a new one based on the provided update.
     *
     * @param update The update object from Telegram containing the user data.
     * @return The app user object.
     */
    public AppUser findOrSaveAppUser( Update update ) {

        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> optionalAppUser = appUserDAO.findByTelegramUserId( telegramUser.getId() );

        if ( optionalAppUser.isEmpty() ) {
            AppUser transientUser = AppUser.builder()
                    .telegramUserId( telegramUser.getId() )
                    .userName( telegramUser.getUserName() )
                    .firstName( telegramUser.getFirstName() )
                    .lastName( telegramUser.getLastName() )
                    .isActive( false )
                    .state( UserState.DEMO_STATE)
                    .userActiveProcess(UserActiveProcess.NONE)
                    .build();

            return appUserDAO.save( transientUser );
        }

        return optionalAppUser.get();
    }

    /**
     * Saves the raw update data to the database for future reference.
     *
     * @param updates The update object to be saved.
     */
    private void saveRawData( List<Update> updates ) {

        List<RawData> rawList = updates.stream()
                .map(update -> RawData.builder().event(update).build())
                .toList();

        rawDataDAO.saveAll(rawList);
    }

    /**
     * Saves the raw update data to the database for future reference.
     *
     * @param update The update object to be saved.
     */
    private void saveRawData(Update update) {

        RawData rawData = RawData.builder()
                .event( update )
                .build();

        rawDataDAO.save( rawData );
    }
}
