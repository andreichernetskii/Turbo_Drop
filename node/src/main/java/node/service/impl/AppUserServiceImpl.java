package node.service.impl;

import common_jpa.dao.AppUserDAO;
import common_jpa.entity.AppUser;
import common_jpa.entity.enums.UserState;
import dto.MailParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import node.service.AppUserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import utils.CryptoTool;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserDAO appUserDAO;

    private final CryptoTool cryptoTool;

    @Value( "${spring.rabbitmq.queues.registration-mail}" )
    private String registrationMailQueue;

    private final RabbitTemplate rabbitTemplate;


    @Override
    public String registerUser( AppUser appUser ) {

        if ( appUser.getIsActive() ) {
            return "Your account already registered.";
        } else if ( appUser.getEmail() != null ) {
            return "Email has already been sent to your mailbox. " +
                    "Follow the link in the letter to confirm your registration.";
        }

        appUser.setState( UserState.WAIT_FOR_EMAIL_STATE );
        appUserDAO.save( appUser );

        return "Please enter your email";
    }

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
            appUser.setState( UserState.BASIC_STATE );
            appUser = appUserDAO.save( appUser );

            String cryptoUserId = cryptoTool.hashOf( appUser.getId() );

            sendRegistrationMail( cryptoUserId, email );

            return "An email has been sent to your mailbox. " +
                    "Follow the link in the email to confirm your registration.";
        } else {
            return "This email is already in use." +
                    "Please enter the correct email address. For cancel operation enter /cancel command.";
        }
    }

    private void sendRegistrationMail( String cryptoUserId, String email ) {

        MailParams mailParams = MailParams.builder()
                .id( cryptoUserId )
                .emailTo( email )
                .build();

        rabbitTemplate.convertAndSend( registrationMailQueue, mailParams );
    }
}
