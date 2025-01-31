package node.service;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface MainService {

    void processTextMessage( Update update );

    void processDocMessage( List<Update> updates );

    void processPhotoMessage( List<Update> updates );

    void activateUser(String encryptedUserId);
}
