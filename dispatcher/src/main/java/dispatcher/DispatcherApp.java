package dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Microservice for initial validation of incoming data
 * and distributing it to appropriate queues in the RabbitMQ message broker.
 */
@EnableAsync
@SpringBootApplication
public class DispatcherApp {
    public static void main(String[] args) {
        SpringApplication.run(DispatcherApp.class);
    }
}
