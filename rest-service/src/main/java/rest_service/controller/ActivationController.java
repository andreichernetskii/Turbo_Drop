package rest_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest_service.service.UserActivationService;

/**
 * This controller handles HTTP requests related to user activation.
 * It provides an endpoint for activating a user based on a unique identifier.
 */
@RequiredArgsConstructor
@RequestMapping( "/user" )
@RestController
public class ActivationController {

    private final UserActivationService userActivationService;

    /**
     * Activates a user based on the provided activation ID.
     * If the activation is successful, a success message is returned with HTTP status 200 (OK).
     * If the activation fails, an HTTP status 500 (Internal Server Error) is returned.
     *
     * @param id The unique identifier for the user activation process.
     * @return A ResponseEntity containing a success message or an error response.
     */
    @GetMapping( "/activation" )
    public ResponseEntity<?> activation( @RequestParam( "id" ) String id ) {

        return (userActivationService.activation( id ))
                ? ResponseEntity.ok().body("Registration finished successful.")
                : ResponseEntity.internalServerError().build();
    }
}
