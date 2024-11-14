package node.service.impl;

import lombok.extern.log4j.Log4j;
import node.service.ConsumerService;
import node.service.ProducerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final ProducerService producerService;

    public ConsumerServiceImpl( ProducerService producerService ) {
        this.producerService = producerService;
    }

    @Override
    @RabbitListener( queues = TEXT_MESSAGE_UPDATE )
    public void consumeTextMessageUpdate( Update update ) {
        log.debug( "NODE: Text message is received." );

        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( message.getChatId().toString() );
        sendMessage.setText( "Hello from Node!" );

        producerService.producerAnswer( sendMessage );
    }

    @Override
    @RabbitListener( queues = DOC_MESSAGE_UPDATE )
    public void consumeDocMessageUpdate( Update update ) {
        log.debug( "NODE: Doc message is received." );
    }

    @Override
    @RabbitListener( queues = PHOTO_MESSAGE_UPDATE )
    public void consumePhotoMessageUpdate( Update update ) {
        log.debug( "NODE: Photo message is received." );
    }
}
