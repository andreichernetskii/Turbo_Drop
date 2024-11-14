package node.service.impl;

import node.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static model.RabbitQueue.ANSWER_MESSAGE_UPDATE;

@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl( RabbitTemplate rabbitTemplate ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void producerAnswer( SendMessage sendMessage ) {
        rabbitTemplate.convertAndSend( ANSWER_MESSAGE_UPDATE, sendMessage );
    }
}
