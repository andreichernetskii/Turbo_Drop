package node.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import node.service.ConsumerService;
import node.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

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

    @Async
    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumeTextMessageUpdate( Update update ) {

        try {
            mainService.processTextMessage( update );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Async
    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.doc-message-update}",
            containerFactory = "rabbitListenerContainerFactory" )
    public void consumeDocMessageUpdate( List<Update> updates ) {

        try {
            mainService.processDocMessage( updates );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Async
    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.photo-message-update}",
            containerFactory = "rabbitListenerContainerFactory" )
    public void consumePhotoMessageUpdate( List<Update> updates ) {

        try {
            mainService.processPhotoMessage( updates );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
