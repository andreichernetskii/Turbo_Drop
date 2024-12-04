package rest_service.service;

import common_jpa.entity.AppDocument;
import common_jpa.entity.AppPhoto;
import common_jpa.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument( String dockId );

    AppPhoto getPhoto( String photoId );

    FileSystemResource getFileSystemResource( BinaryContent binaryContent );
}
