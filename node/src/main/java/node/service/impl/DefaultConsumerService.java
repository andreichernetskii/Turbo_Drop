package node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import node.service.ConsumerService;
import node.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Implementation of the {@link ConsumerService} interface for consuming updates from RabbitMQ.
 * This service listens for updates related to text, document, and photo messages and delegates processing
 * to the {@link MainService}.
 */
@RequiredArgsConstructor
@Log4j
@Service
public class DefaultConsumerService implements ConsumerService {

    private final MainService mainService;

    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.text-message-update}" )
    public void consumeTextMessageUpdate( Update update ) {

        log.debug( "NODE: Text message is received." );
        mainService.processTextMessage( update );
    }

    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.doc-message-update}" )
    public void consumeDocMessageUpdate( Update update ) {

        log.debug( "NODE: Doc message is received." );
        mainService.processDocMessage( update );
    }

    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.photo-message-update}" )
    public void consumePhotoMessageUpdate( Update update ) {

        log.debug( "NODE: Photo message is received." );
        mainService.processPhotoMessage( update );
    }
}
