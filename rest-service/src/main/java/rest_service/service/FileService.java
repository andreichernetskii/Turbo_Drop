package rest_service.service;

import common.entity.AppDocument;
import common.entity.AppPhoto;

public interface FileService {

    AppDocument getDocument( String dockId );

    AppPhoto getPhoto( String photoId );

}
