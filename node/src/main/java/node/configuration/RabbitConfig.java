package node.configuration;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

    /**
     * Configures a {@link SimpleRabbitListenerContainerFactory} bean with custom settings.
     *
     * This factory enables batch message processing for RabbitMQ consumers.
     * By setting {@code setBatchListener(true)} and {@code setConsumerBatchEnabled(true)},
     * it allows consumers to process messages in batches, improving throughput and reducing
     * overhead when handling a large volume of messages.
     *
     * Key configurations:
     * <ul>
     *     <li>{@code setBatchListener(true)}: Enables batch message listeners.</li>
     *     <li>{@code setConsumerBatchEnabled(true)}: Activates batch message consumption.</li>
     *     <li>{@code setBatchSize(100)}: Sets the maximum number of messages per batch.</li>
     *     <li>{@code setMessageConverter(jsonMessageConverter())}: Configures the JSON message
     *         converter for serialization and deserialization of messages.</li>
     *     <li>{@code setConnectionFactory(connectionFactory)}: Associates the connection
     *         factory to manage connections with RabbitMQ.</li>
     * </ul>
     *
     * @param connectionFactory the RabbitMQ {@link ConnectionFactory} used to manage connections.
     * @return an instance of {@link SimpleRabbitListenerContainerFactory} configured for batch processing.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setBatchListener(true);
        factory.setConsumerBatchEnabled(true);
        factory.setBatchSize(100);

        return factory;
    }
}
