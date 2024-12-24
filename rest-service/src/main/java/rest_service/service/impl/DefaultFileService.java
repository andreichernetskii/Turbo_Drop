package rest_service.service.impl;

import common.dao.AppDocumentDAO;
import common.dao.AppPhotoDAO;
import common.entity.AppDocument;
import common.entity.AppPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import rest_service.service.FileService;
import rest_service.utils.Decoder;

@Log4j
@RequiredArgsConstructor
@Service
public class DefaultFileService implements FileService {

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final Decoder decoder;

    @Override
    public AppDocument getDocument( String hash ) {

        Long id = decoder.idOf( hash );
        return ( id != null ) ? appDocumentDAO.findById( id ).orElse( null ) : null;
    }

    @Override
    public AppPhoto getPhoto( String hash ) {

        Long id = decoder.idOf( hash );
        return ( id != null ) ? appPhotoDAO.findById( id ).orElse( null ) : null;
    }
}
