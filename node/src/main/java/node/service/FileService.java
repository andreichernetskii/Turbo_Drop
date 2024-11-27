package node.service;

import common_jpa.entity.AppDocument;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc( Message externalMessage );
}
