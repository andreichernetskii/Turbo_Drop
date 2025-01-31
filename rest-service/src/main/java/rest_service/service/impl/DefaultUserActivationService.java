package rest_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rest_service.configuration.RabbitConfig;
import rest_service.service.MailConfirmedProducer;
import rest_service.service.UserActivationService;

/**
 * Service implementation for activating user accounts.
 * Provides functionality to decode encrypted user IDs and update user activation status in the database.
 */
@RequiredArgsConstructor
@Service
public class DefaultUserActivationService implements UserActivationService {

    private final MailConfirmedProducer mailConfirmedProducer;

    private final RabbitConfig rabbitConfig;

    /**
     * Sends an encrypted user ID to a queue for user activation.
     *
     * This method receives an encrypted user ID, and sends it to a RabbitMQ queue for further
     * processing, where the user will be activated.
     *
     * @param encryptedUserId The encrypted user ID to be sent for activation.
     */
    @Override
    public void activation( String encryptedUserId ) {
        mailConfirmedProducer.produce(rabbitConfig.getMailConfirmedMessageUpdateQueue(), encryptedUserId);
    }
}
