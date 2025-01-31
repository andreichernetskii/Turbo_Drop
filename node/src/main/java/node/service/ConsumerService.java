package node.service;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface ConsumerService {

    void consumeTextMessageUpdate( Update update );

    void consumeDocMessageUpdate( List<Update> updates );

    void consumePhotoMessageUpdate( List<Update> updates );

    void consumeMailConfirmedMessage(String encryptedUserId);
}
