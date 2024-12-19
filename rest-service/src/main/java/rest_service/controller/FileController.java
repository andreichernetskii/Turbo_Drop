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

@Log4j
@RequiredArgsConstructor
@RequestMapping( "/file" )
@RestController
public class FileController {

    private final FileService fileService;

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
