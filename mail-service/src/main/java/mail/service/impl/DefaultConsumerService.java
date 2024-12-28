package mail.service.impl;

import common.dto.MailParams;
import lombok.RequiredArgsConstructor;
import mail.service.ConsumerService;
import mail.service.MailSenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ConsumerService} interface.
 *
 * This service listens to RabbitMQ queues for incoming messages with registration mail parameters
 * and triggers the sending of activation emails using {@link MailSenderService}.
 */
@RequiredArgsConstructor
@Service
public class DefaultConsumerService implements ConsumerService {

    private final MailSenderService mailSenderService;

    /**
     * Listens to the RabbitMQ queue for registration mail parameters and triggers email sending.
     *
     * The queue name is defined in the application configuration under the property
     * `spring.rabbitmq.queues.registration-mail`. The method automatically deserializes the
     * incoming message into a {@link MailParams} object and passes it to the {@link MailSenderService}.
     *
     * @param mailParams parameters for the email, including recipient's email address and activation ID.
     *                   This object is automatically deserialized from the message consumed from the queue.
     */
    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.registration-mail}" )
    public void consumeRegistrationMail( MailParams mailParams ) {
        mailSenderService.send( mailParams );
    }
}
