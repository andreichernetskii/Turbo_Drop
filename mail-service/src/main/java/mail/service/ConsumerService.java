package mail.service;

import common.dto.MailParams;

public interface ConsumerService {

    void consumeRegistrationMail( MailParams mailParams );
}
