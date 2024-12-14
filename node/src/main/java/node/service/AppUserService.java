package node.service;

import common_jpa.entity.AppUser;

public interface AppUserService {
    String registerUser( AppUser appUser );

    String setEmail( AppUser appUser, String email );
}
