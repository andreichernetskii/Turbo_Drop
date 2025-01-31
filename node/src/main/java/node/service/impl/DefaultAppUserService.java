package node.service.impl;

import common.dao.AppUserDAO;
import common.entity.AppUser;
import common.entity.enums.UserActiveProcess;
import common.dto.MailParams;
import common.entity.enums.UserState;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import node.service.AppUserService;
import node.utils.Decoder;
import org.hashids.Hashids;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

/**
 * Implementation of the {@link AppUserService} interface.
 * This service manages user registration, including email validation and sending registration emails.
 */
@RequiredArgsConstructor
@Log4j
@Service
public class DefaultAppUserService implements AppUserService {

    private final AppUserDAO appUserDAO;

    private final Hashids hashids;

    private final Decoder decoder;

    @Value( "${spring.rabbitmq.queues.registration-mail}" )
    private String registrationMailQueue;

    private final RabbitTemplate rabbitTemplate;

    /**
     * Registers a new user in the system.
     * If the user is already active or has a pending email confirmation, appropriate messages are returned.
     * Otherwise, the user's state is updated to wait for email input.
     *
     * @param appUser the user entity to register.
     * @return a message indicating the next step or the current registration status.
     */
    @Override
    public String registerUser( AppUser appUser ) {

        if ( appUser.getIsActive() ) {
            return "Your account already registered.";
        } else if ( appUser.getEmail() != null ) {
            return "Email has already been sent to your mailbox. " +
                    "Follow the link in the letter to confirm your registration.";
        }

        appUser.setUserActiveProcess(UserActiveProcess.REGISTRATION_IN_PROCESS);
        appUserDAO.save( appUser );

        return "Registration process is started.\nPlease enter your email";
    }

    /**
     * Sets the email for a user, validates the email, and sends a registration confirmation email if valid.
     * If the email is already in use, an appropriate message is returned.
     *
     * @param appUser the user entity to associate with the email.
     * @param email the email address to be validated and associated with the user.
     * @return a message indicating the result of the email setting process.
     */
    @Override
    public String setEmail( AppUser appUser, String email ) {

        try {
            new InternetAddress( email ).validate();
        } catch ( AddressException exception ) {
            return "Please enter the correct email address. For cancel operation enter /cancel command.";
        }

        Optional<AppUser> optionalAppUser = appUserDAO.findByEmail( email );

        if ( optionalAppUser.isEmpty() ) {
            appUser.setEmail( email );
//            appUser.setState( UserState.BASIC_STATE );
            appUser = appUserDAO.save( appUser );

            String cryptoUserId = hashids.encode( appUser.getId() );

            sendRegistrationMail( cryptoUserId, email );

            return "An email has been sent to your mailbox. " +
                    "Follow the link in the email to confirm your registration.";
        } else {
            return "This email is already in use." +
                    "Please enter the correct email address. For cancel operation enter /cancel command.";
        }
    }

    @Override
    public void activateUser(String encryptedUserId) {

        Long userId = decoder.idOf(encryptedUserId);

        Optional<AppUser> optionalAppUser = appUserDAO.findById(userId);

        if (optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            appUser.setUserActiveProcess(UserActiveProcess.NONE);
            appUser.setIsActive(true);
            appUser.setState(UserState.BASIC_STATE);

            appUserDAO.save(appUser);
        }
    }

    /**
     * Sends a registration confirmation email to the specified address.
     * The email contains a unique user identifier for account verification.
     *
     * @param cryptoUserId the encoded unique identifier of the user.
     * @param email the email address to send the registration message to.
     */
    private void sendRegistrationMail( String cryptoUserId, String email ) {

        MailParams mailParams = MailParams.builder()
                .id( cryptoUserId )
                .emailTo( email )
                .build();

        rabbitTemplate.convertAndSend( registrationMailQueue, mailParams );
    }
}
