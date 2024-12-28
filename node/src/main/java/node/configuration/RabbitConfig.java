package node.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ messaging setup.
 *
 * This class provides configuration for message conversion, enabling the application
 * to serialize and deserialize messages in JSON format when interacting with RabbitMQ.
 */
@Configuration
public class RabbitConfig {

    /**
     * Configures a {@link MessageConverter} bean for JSON serialization and deserialization.
     *
     * The {@link Jackson2JsonMessageConverter} is used to automatically convert Java objects
     * to JSON when sending messages to RabbitMQ and to convert JSON to Java objects when
     * receiving messages. This simplifies communication between services.
     *
     * @return an instance of {@link Jackson2JsonMessageConverter}.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
