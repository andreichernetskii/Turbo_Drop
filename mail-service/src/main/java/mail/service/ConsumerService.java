package mail.service;

import dto.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail( MailParams mailParams );
}
