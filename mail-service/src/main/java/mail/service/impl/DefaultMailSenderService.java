package mail.service.impl;

import common.dto.MailParams;
import lombok.RequiredArgsConstructor;
import mail.service.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link MailSenderService} interface.
 *
 * This service handles the sending of activation emails to users. It uses Spring's {@link JavaMailSender}
 * for email delivery and constructs the activation email content dynamically.
 */
@RequiredArgsConstructor
@Service
public class DefaultMailSenderService implements MailSenderService {

    private final JavaMailSender javaMailSender;

    @Value( "${spring.mail.username}" )
    private String emailFrom;

    @Value( "${service.activation.uri}" )
    private String activationServiceUri;

    /**
     * Sends an account activation email to the specified recipient.
     *
     * The method dynamically constructs the email subject and body based on the provided {@link MailParams},
     * which include the recipient's email address and a unique activation ID.
     *
     * @param mailParams parameters for the email, including recipient's email address and activation ID.
     */
    @Override
    public void send( MailParams mailParams ) {

        String subject = "Account activation";
        String messageBody = getActivationMailBody( mailParams.getId() );
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom( emailFrom );
        mailMessage.setTo( emailTo );
        mailMessage.setSubject( subject );
        mailMessage.setText( messageBody );

        javaMailSender.send( mailMessage );
    }

    /**
     * Constructs the body of the activation email, including a link with the activation ID.
     *
     * The activation link is based on the base URI defined in the application configuration file and
     * the user's unique activation ID.
     *
     * @param id unique identifier for the user's activation.
     * @return the activation email body containing the verification link.
     */
    private String getActivationMailBody( String id ) {

        String msg = String.format(
                "To verify your account, please follow the link:\n%s ", activationServiceUri
        );
        return msg.replace( "{id}", id );
    }
}
