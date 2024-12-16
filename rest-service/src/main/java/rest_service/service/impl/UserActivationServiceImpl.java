package rest_service.service.impl;

import common_jpa.dao.AppUserDAO;
import common_jpa.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rest_service.service.UserActivationService;
import utils.CryptoTool;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;

    private final CryptoTool cryptoTool;

    @Override
    public boolean activation( String cryptoUserId ) {

        Long userId = cryptoTool.idOf( cryptoUserId );
        Optional<AppUser> optionalAppUser = appUserDAO.findById( userId );

        if ( optionalAppUser.isPresent() ) {
            AppUser appUser = optionalAppUser.get();
            appUser.setIsActive( true );
            appUserDAO.save( appUser );

            return true;
        }
        return false;
    }
}
