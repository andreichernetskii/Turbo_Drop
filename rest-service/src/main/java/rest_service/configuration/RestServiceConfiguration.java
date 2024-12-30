package rest_service.configuration;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up application-wide beans.
 *
 * This class defines the configuration for the `Hashids` library,
 * which is used to encode and decode unique identifiers into short hash strings.
 */
@Configuration
public class RestServiceConfiguration {

    @Value( "${salt}" )
    private String salt;

    /**
     * Creates and configures a {@link Hashids} instance with the provided salt.
     *
     * The `Hashids` library is used to generate unique, obfuscated, and reversible hash strings
     * based on numeric identifiers. Salt ensures the generated hashes are unique for the application.
     *
     * @return a configured instance of {@link Hashids}.
     */
    @Bean
    public Hashids getHashids() {
        return new Hashids( salt );
    }
}
