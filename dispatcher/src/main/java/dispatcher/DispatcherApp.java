package dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservice for initial validation of incoming data
 * and distributing it to appropriate queues in the RabbitMQ message broker.
 */
@SpringBootApplication
public class DispatcherApp {
    public static void main(String[] args) {
        SpringApplication.run(DispatcherApp.class);
    }
}
