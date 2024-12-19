package node.service;

import common.entity.AppDocument;
import common.entity.AppPhoto;
import node.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {

    AppDocument processDoc( Message telegramMessage );

    AppPhoto processPhoto( Message telegramMessage );

    String generateLing( Long docId, LinkType linkType );
}
