package dispatcher.service.impl;

import dispatcher.service.UpdateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Implementation of the {@link UpdateProducer} interface for producing messages to RabbitMQ queues.
 *
 * This class is responsible for sending {@link Update} objects, received from the Telegram API,
 * to the specified RabbitMQ queue for further processing.
 */
@Log4j
@RequiredArgsConstructor
@Service
public class DefaultUpdateProducer implements UpdateProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Produces a message to a RabbitMQ queue.
     *
     * This method takes an incoming {@link Update} object and sends it to the specified RabbitMQ queue.
     * The queue name is passed as a parameter. The method logs the message text for debugging purposes.
     *
     * @param rabbitQueue - the name of the RabbitMQ queue to which the message should be sent.
     * @param update      - the {@link Update} object containing data from Telegram. It may include
     *                      messages, commands, or other types of updates from chats.
     */
    @Async
    @Override
    public void produce( String rabbitQueue, Update update ) {

        rabbitTemplate.convertAndSend( rabbitQueue, update );
    }
}





