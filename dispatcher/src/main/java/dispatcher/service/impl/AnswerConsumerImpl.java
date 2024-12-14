package dispatcher.service.impl;

import dispatcher.controller.UpdateProcessor;
import dispatcher.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateProcessor updateProcessor;

    public AnswerConsumerImpl( UpdateProcessor updateProcessor ) {
        this.updateProcessor = updateProcessor;
    }

    @Override
    @RabbitListener( queues = ANSWER_MESSAGE_UPDATE )
    public void consume( SendMessage sendMessage ) {
        updateProcessor.setView( sendMessage );
    }
}
