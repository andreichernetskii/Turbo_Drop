package rest_service.service.impl;

import common.dao.AppUserDAO;
import common.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rest_service.service.UserActivationService;
import rest_service.utils.Decoder;

import java.util.Optional;

/**
 * Service implementation for activating user accounts.
 * Provides functionality to decode encrypted user IDs and update user activation status in the database.
 */
@RequiredArgsConstructor
@Service
public class DefaultUserActivationService implements UserActivationService {

    private final AppUserDAO appUserDAO;

    private final Decoder decoder;

    /**
     * Activates a user account based on the provided encrypted user ID.
     * Decodes the encrypted ID, retrieves the corresponding user entity, and marks it as active.
     *
     * @param cryptoUserId The encrypted user ID.
     * @return {@code true} if the user was successfully activated; {@code false} otherwise (e.g., user not found).
     */
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
