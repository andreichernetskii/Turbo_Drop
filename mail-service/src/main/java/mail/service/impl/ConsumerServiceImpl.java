package mail.service.impl;

import dto.MailParams;
import lombok.RequiredArgsConstructor;
import mail.service.ConsumerService;
import mail.service.MailSenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener( queues = "${spring.rabbitmq.queues.registration-mail}" )
    public void consumeRegistrationMail( MailParams mailParams ) {
        mailSenderService.send( mailParams );
    }
}
