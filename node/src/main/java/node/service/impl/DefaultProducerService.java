package node.service.impl;

import lombok.RequiredArgsConstructor;
import node.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * This service is responsible for sending messages to a RabbitMQ queue for further processing.
 * It provides functionality to send answer messages via the `RabbitTemplate` to the specified queue.
 */
@RequiredArgsConstructor
@Service
public class DefaultProducerService implements ProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;


    /**
     * Sends the provided SendMessage object to the RabbitMQ queue for processing.
     * The message is serialized and sent to the queue specified by the answerMessageQueue property.
     *
     * @param sendMessage The SendMessage object containing the message to be sent to the queue.
     */
    @Override
    public void producerAnswer( SendMessage sendMessage ) {
        rabbitTemplate.convertAndSend( answerMessageQueue, sendMessage );
    }
}
