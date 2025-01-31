package rest_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rest_service.service.MailConfirmedProducer;

@RequiredArgsConstructor
@Service
public class DefaultMailConfirmedProducer implements MailConfirmedProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Sends an encrypted user ID to a specified RabbitMQ queue.
     *
     * This method receives an encrypted user ID and sends it to the provided RabbitMQ queue.
     * The queue name is passed as a parameter.
     *
     * @param rabbitQueue - the name of the RabbitMQ queue to which the message should be sent.
     * @param encryptedUserId - the encrypted ID of the user to be sent to the queue.
     */
    @Async
    @Override
    public void produce(String rabbitQueue, String encryptedUserId) {
        rabbitTemplate.convertAndSend(rabbitQueue, encryptedUserId);
    }
}
