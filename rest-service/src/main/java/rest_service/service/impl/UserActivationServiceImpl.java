package rest_service.service.impl;

import common.dao.AppUserDAO;
import common.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rest_service.service.UserActivationService;
import rest_service.utils.Decoder;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;

    private final Decoder decoder;

    @Override
    public boolean activation( String cryptoUserId ) {

        Long userId = decoder.idOf( cryptoUserId );
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
