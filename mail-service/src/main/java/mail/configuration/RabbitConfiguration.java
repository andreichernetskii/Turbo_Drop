package mail.configuration;

import lombok.Getter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ integration in the Mail service.
 *
 * This class defines the queue for handling registration email messages and configures
 * a JSON message converter for serializing and deserializing messages sent to and received from RabbitMQ.
 */
@Getter
@Configuration
public class RabbitConfiguration {

    @Value( "${spring.rabbitmq.queues.registration-mail}" )
    private String registrationMailQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessage() {
        return new Queue( registrationMailQueue);
    }
}
