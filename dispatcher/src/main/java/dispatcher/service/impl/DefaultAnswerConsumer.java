package dispatcher.service.impl;

import dispatcher.controller.UpdateProcessor;
import dispatcher.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Implementation of the {@link AnswerConsumer} interface for processing messages from RabbitMQ.
 *
 * This class listens to a specific RabbitMQ queue for messages containing {@link SendMessage} objects
 * and delegates their processing to the {@link UpdateProcessor}.
 */
@RequiredArgsConstructor
@Service
public class DefaultAnswerConsumer implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    /**
     * Consumes messages from the RabbitMQ queue defined in the `spring.rabbitmq.queues.answer-message` property.
     *
     * This method is triggered automatically whenever a new message appears in the queue. It processes
     * the incoming {@link SendMessage} object and sends it to the chat via the Telegram bot.
     *
     * @param sendMessage - the message to be sent, represented as a {@link SendMessage} object.
     *                      It contains details such as chat ID and the text to send.
     */
    @Async
    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.answer-message}" )
    public void consume( SendMessage sendMessage ) {
        updateProcessor.setView( sendMessage );
    }
}
