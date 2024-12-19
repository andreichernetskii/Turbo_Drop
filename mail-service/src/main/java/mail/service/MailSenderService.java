package mail.service;

import common.dto.MailParams;

public interface MailSenderService {

    void send( MailParams mailParams );
}
