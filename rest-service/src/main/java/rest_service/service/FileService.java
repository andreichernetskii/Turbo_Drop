package rest_service.service;

import common_jpa.entity.AppDocument;
import common_jpa.entity.AppPhoto;

public interface FileService {
    AppDocument getDocument( String dockId );

    AppPhoto getPhoto( String photoId );

}
