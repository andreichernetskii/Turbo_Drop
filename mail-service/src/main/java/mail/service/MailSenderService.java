package mail.service;

import mail.dto.MailParams;

public interface MailSenderService {
    void send( MailParams mailParams );
}
