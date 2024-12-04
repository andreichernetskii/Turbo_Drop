package rest_service.service.impl;

import common_jpa.dao.AppDocumentDAO;
import common_jpa.dao.AppPhotoDAO;
import common_jpa.entity.AppDocument;
import common_jpa.entity.AppPhoto;
import common_jpa.entity.BinaryContent;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import rest_service.service.FileService;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceImpl( AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO ) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument getDocument( String dockId ) {
        //todo: add decrypto of hash-string
        Long id = Long.parseLong( dockId );
        return appDocumentDAO.findById( id ).orElse( null );
    }

    @Override
    public AppPhoto getPhoto( String photoId ) {
        //todo: add decrypto of hash-string
        Long id = Long.parseLong( photoId );
        return appPhotoDAO.findById( id ).orElse( null );
    }

    @Override
    public FileSystemResource getFileSystemResource( BinaryContent binaryContent ) {
        try {
            //todo: add name generation of temp file
            File tempFile = File.createTempFile( "tempFile", ".bin" );
            tempFile.deleteOnExit();
            FileUtils.writeByteArrayToFile( tempFile, binaryContent.getFileAsArrayOfBytes() );

            return new FileSystemResource( tempFile );
        } catch ( IOException e ) {
            log.error( e );
            return null;
        }
    }
}
