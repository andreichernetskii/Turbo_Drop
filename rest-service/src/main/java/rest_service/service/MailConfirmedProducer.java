package rest_service.service;

public interface MailConfirmedProducer {
    void produce( String rabbitQueue, String cryptoUserId );
}
