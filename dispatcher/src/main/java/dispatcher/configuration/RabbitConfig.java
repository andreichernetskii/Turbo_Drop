package dispatcher.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class RabbitConfig {

    @Value( "${spring.rabbitmq.text-message-update}" )
    private String textMessageUpdateQueue;

    @Value( "${spring.rabbitmq.doc-message-update}" )
    private String docMessageUpdateQueue;

    @Value( "${spring.rabbitmq.photo-message-update}" )
    private String photoMessageUpdateQueue;

    @Value( "${spring.rabbitmq.answer-message}" )
    private String answerMessageQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue( textMessageUpdateQueue );
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue( docMessageUpdateQueue );
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue( photoMessageUpdateQueue );
    }

    @Bean
    public Queue answerMessageQueue() {
        return new Queue( answerMessageQueue );
    }

}
