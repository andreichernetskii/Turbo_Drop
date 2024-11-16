package node.service.impl;

import node.dao.RawDataDAO;
import node.entity.RawData;
import node.service.MainService;
import node.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;

    public MainServiceImpl( RawDataDAO rawDataDAO, ProducerService producerService ) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage( Update update ) {
        saveRawData( update );
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId( message.getChatId().toString() );
        sendMessage.setText( "Hello from Node!" );

        producerService.producerAnswer( sendMessage );
    }

    private void saveRawData( Update update ) {
        RawData rawData = RawData.builder()
                .event( update )
                .build();

        rawDataDAO.save( rawData );
    }
}
