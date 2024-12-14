package rest_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest_service.service.UserActivationService;

@RequestMapping( "/user" )
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;

    public ActivationController( UserActivationService userActivationService ) {
        this.userActivationService = userActivationService;
    }

    @GetMapping( "/activation" )
    public ResponseEntity<?> activation( @RequestParam( "id" ) String id ) {
        return (userActivationService.activation( id ))
                ? ResponseEntity.ok().body("Registration finished successful.")
                : ResponseEntity.internalServerError().build();
    }
}
