package mail.service;

import dto.MailParams;

public interface MailSenderService {
    void send( MailParams mailParams );
}
