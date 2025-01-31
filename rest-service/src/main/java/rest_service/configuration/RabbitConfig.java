package rest_service.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ setup.
 *
 * This class defines queues for processing various types of messages and configures a JSON message converter
 * for serializing and deserializing messages sent to and received from RabbitMQ.
 */
@Getter
@Configuration
public class RabbitConfig {

    @Value( "${spring.rabbitmq.queues.mail-confirmed-message-update}" )
    private String mailConfirmedMessageUpdateQueue;


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue mailConfirmedMessageQueue() {
        return new Queue( mailConfirmedMessageUpdateQueue );
    }

}
