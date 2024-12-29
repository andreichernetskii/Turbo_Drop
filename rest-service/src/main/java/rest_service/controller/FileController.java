package rest_service.controller;

import common.entity.AppDocument;
import common.entity.AppPhoto;
import common.entity.BinaryContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest_service.service.FileService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * REST controller for handling file-related operations.
 * Provides endpoints for downloading documents and photos.
 */
@Log4j
@RequiredArgsConstructor
@RequestMapping( "/file" )
@RestController
public class FileController {

    private final FileService fileService;

    /**
     * Handles HTTP GET requests for downloading a document.
     * If the requested document exists, it is returned as a file download.
     * Sets the HTTP response status to:
     * - 200 (OK) if the document is successfully retrieved.
     * - 400 (Bad Request) if the document with the provided ID does not exist.
     * - 500 (Internal Server Error) if an I/O exception occurs during the process.
     *
     * @param id       The unique identifier of the document.
     * @param response The HTTP response object used to write the file to the client.
     */
    @GetMapping( "/get-doc" )
    public void getDoc( @RequestParam( "id" ) String id, HttpServletResponse response ) {

        AppDocument doc = fileService.getDocument( id );

        if ( doc == null ) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }

        response.setContentType( MediaType.parseMediaType( doc.getMimeType() ).toString() );
        response.setHeader( "Content-disposition", "attachment; filename=" + doc.getDocName() );
        response.setStatus( HttpServletResponse.SC_OK );

        BinaryContent binaryContent = doc.getBinaryContent();

        try ( ServletOutputStream outputStream = response.getOutputStream() ) {
            outputStream.write( binaryContent.getFileAsArrayOfBytes() );
        } catch ( IOException exception ) {
            log.error( exception );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }

    /**
     * Handles HTTP GET requests for downloading a photo.
     * If the requested photo exists, it is returned as a file download in JPEG format.
     * Sets the HTTP response status to:
     * - 200 (OK) if the photo is successfully retrieved.
     * - 400 (Bad Request) if the photo with the provided ID does not exist.
     * - 500 (Internal Server Error) if an I/O exception occurs during the process.
     *
     * @param id       The unique identifier of the photo.
     * @param response The HTTP response object used to write the file to the client.
     */
    @GetMapping( "/get-photo" )
    public void getPhoto( @RequestParam( "id" ) String id, HttpServletResponse response ) {

        //todo: add ControllerAdvice for forming a badRequest
        AppPhoto photo = fileService.getPhoto( id );

        if ( photo == null ) {
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            return;
        }

        response.setContentType( MediaType.IMAGE_JPEG.toString() );
        response.setHeader( "Content-disposition", "attachment;" );
        response.setStatus( HttpServletResponse.SC_OK );

        BinaryContent binaryContent = photo.getBinaryContent();

        try ( ServletOutputStream outputStream = response.getOutputStream() ) {
            outputStream.write( binaryContent.getFileAsArrayOfBytes() );
        } catch ( IOException exception ) {
            log.error( exception );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }
}
