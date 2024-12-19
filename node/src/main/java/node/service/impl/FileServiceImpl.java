package node.service.impl;

import common.dao.AppDocumentDAO;
import common.dao.AppPhotoDAO;
import common.dao.BinaryContentDAO;
import common.entity.AppDocument;
import common.entity.AppPhoto;
import common.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import node.exceptinos.UploadFileException;
import node.service.FileService;
import node.service.enums.LinkType;
import org.hashids.Hashids;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    @Value( "${token}" )
    private String token;

    @Value( "${service.file_info.uri}" )
    private String fileInfoUri;

    @Value( "${service.file_storage.uri}" )
    private String fileStorageUri;

    @Value( "${rest-service.link.address}" )
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final BinaryContentDAO binaryContentDAO;

    private final Hashids hashids;

    @Override
    public AppDocument processDoc( Message telegramMessage ) {

        Document telegramDoc = telegramMessage.getDocument();
        ResponseEntity<String> response = getFilePath( telegramDoc.getFileId() );

        if ( response.getStatusCode() == HttpStatus.OK ) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent( response );
            AppDocument transientAppDoc = buildTransientAppDoc( telegramDoc, persistentBinaryContent );

            return appDocumentDAO.save( transientAppDoc );
        } else {
            throw new UploadFileException( "Bad response from telegram service: " + response );
        }
    }

    @Override
    public AppPhoto processPhoto( Message telegramMessage ) {

        // telegramAPI saves photos in few size variants
        // getting last variant size of loaded photo
        int photoSizeCount = telegramMessage.getPhoto().size();
        int photoIndex = ( photoSizeCount > 1 ) ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get( photoIndex );
        ResponseEntity<String> response = getFilePath( telegramPhoto.getFileId() );

        if ( response.getStatusCode() == HttpStatus.OK ) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent( response );
            AppPhoto transientAppPhoto = buildTransientAppPhoto( telegramPhoto, persistentBinaryContent );

            return appPhotoDAO.save( transientAppPhoto );
        } else {
            throw new UploadFileException( "Bad response from telegram service: " + response );
        }
    }

    @Override
    public String generateLing( Long docId, LinkType linkType ) {

        String hash = hashids.encode( docId );
        return "http://" + linkAddress + linkType + "?id=" + hash;
    }

    private BinaryContent getPersistentBinaryContent( ResponseEntity<String> response ) {

        String filePath = getFilePath( response );
        BinaryContent transientBinaryContent = BinaryContent
                .builder()
                .fileAsArrayOfBytes( downloadFileInByte( filePath ) )
                .build();

        return binaryContentDAO.save( transientBinaryContent );
    }

    private static String getFilePath( ResponseEntity<String> response ) {

        JSONObject jsonObject = new JSONObject( response.getBody() );

        return String.valueOf(
                jsonObject
                        .getJSONObject( "result" )
                        .getString( "file_path" )
        );
    }

    private AppDocument buildTransientAppDoc( Document telegramDoc, BinaryContent persistentBinaryContent ) {

        return AppDocument.builder()
                .telegramFileId( telegramDoc.getFileId() )
                .docName( telegramDoc.getFileName() )
                .binaryContent( persistentBinaryContent )
                .mimeType( telegramDoc.getMimeType() )
                .fileSize( telegramDoc.getFileSize() )
                .build();
    }

    private AppPhoto buildTransientAppPhoto( PhotoSize telegramPhoto, BinaryContent persistentBinaryContent ) {

        return AppPhoto.builder()
                .telegramFileId( telegramPhoto.getFileId() )
                .binaryContent( persistentBinaryContent )
                .fileSize( telegramPhoto.getFileSize() )
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
                token,
                fileId
        );
    }

    private byte[] downloadFileInByte( String filePath ) {

        String fullUri = fileStorageUri
                .replace( "{token}", token )
                .replace( "{filePath}", filePath );

        URL urlObj = null;

        try {
            urlObj = new URL( fullUri );
        } catch ( MalformedURLException exception ) {
            throw new UploadFileException( exception );
        }

        //todo: refacto in case downloading big files. how to do this
        //todo: maybe some optimisation here
        // the all file will be saved in ram without chanks separation
        try ( InputStream inputStream = urlObj.openStream() ) {
            return inputStream.readAllBytes();
        } catch ( IOException exception ) {
            throw new UploadFileException( exception );
        }
    }
}
