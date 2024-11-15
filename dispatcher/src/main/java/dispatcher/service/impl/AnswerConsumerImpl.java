package dispatcher.service.impl;

import dispatcher.controller.UpdateController;
import dispatcher.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl( UpdateController updateController ) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener( queues = ANSWER_MESSAGE_UPDATE )
    public void consume( SendMessage sendMessage ) {
        updateController.setView( sendMessage );
    }
}
