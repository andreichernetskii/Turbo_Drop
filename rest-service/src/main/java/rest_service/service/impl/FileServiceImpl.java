package rest_service.service.impl;

import common_jpa.dao.AppDocumentDAO;
import common_jpa.dao.AppPhotoDAO;
import common_jpa.entity.AppDocument;
import common_jpa.entity.AppPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import rest_service.service.FileService;
import utils.CryptoTool;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentDAO appDocumentDAO;

    private final AppPhotoDAO appPhotoDAO;

    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument( String hash ) {

        Long id = cryptoTool.idOf( hash );
        return ( id != null ) ? appDocumentDAO.findById( id ).orElse( null ) : null;
    }

    @Override
    public AppPhoto getPhoto( String hash ) {

        Long id = cryptoTool.idOf( hash );
        return ( id != null ) ? appPhotoDAO.findById( id ).orElse( null ) : null;
    }
}
