package node.service;

import common.entity.AppUser;

public interface AppUserService {

    String registerUser( AppUser appUser );

    String setEmail( AppUser appUser, String email );

    void activateUser(String encryptedUserId);
}
