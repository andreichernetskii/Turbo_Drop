package node.service.impl;

import common_jpa.dao.AppDocumentDAO;
import common_jpa.dao.BinaryContentDAO;
import common_jpa.entity.AppDocument;
import common_jpa.entity.BinaryContent;
import lombok.extern.log4j.Log4j;
import node.exceptinos.UploadFileException;
import node.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value( "${bot_token}" )
    private String botToken;
    @Value( "${service.file_info.uri}" )
    private String fileInfoUri;
    @Value( "${service.file_storage.uri}" )
    private String fileStorageUri;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl( AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO ) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument processDoc( Message telegramMessage ) {
        ResponseEntity<String> response = getFilePath( telegramMessage.getDocument().getFileId() );

        if ( response.getStatusCode() == HttpStatus.OK ) {
            JSONObject jsonObject = new JSONObject( response.getBody() );
            String filePath = String.valueOf(
                    jsonObject
                            .getJSONObject( "result" )
                            .getString( "file_path" )
            );
            BinaryContent transientBinaryContent = BinaryContent
                    .builder()
                    .fileAsArrayOfBytes( downloadFileInByte( filePath ) )
                    .build();
            BinaryContent persistentBinaryContent = binaryContentDAO.save( transientBinaryContent );
            AppDocument transientAppDoc = buildTransientAppDoc(
                    telegramMessage.getDocument(), persistentBinaryContent );

            return appDocumentDAO.save( transientAppDoc );
        } else {
            throw new UploadFileException( "Bad response from telegram service: " + response );
        }
    }

    private AppDocument buildTransientAppDoc( Document telegramDoc, BinaryContent persistentBinaryContent ) {
        return AppDocument
                .builder()
                .telegramFileId( telegramDoc.getFileId() )
                .docName( telegramDoc.getFileName() )
                .binaryContent( persistentBinaryContent )
                .mimeType( telegramDoc.getMimeType() )
                .fileSize( telegramDoc.getFileSize() )
                .build();
    }

    private ResponseEntity<String> getFilePath( String fileId ) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>( headers );

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                botToken,
                fileId
        );
    }

    private byte[] downloadFileInByte( String filePath ) {
        String fullUri = filePath
                .replace( "{token}", botToken )
                .replace( "{filePath}", filePath );

        URL urlObj = null;

        try {
            urlObj = new URL( fullUri );
        } catch ( MalformedURLException exception ) {
            throw new UploadFileException( exception );
        }

        //todo: refacto in case downloading big files. how to do this
        try ( InputStream inputStream = urlObj.openStream() ) {
            return inputStream.readAllBytes();
        } catch ( IOException exception ) {
            throw new UploadFileException( exception );
        }
    }
}
