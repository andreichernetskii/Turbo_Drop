package rest_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories( basePackages = { "common.dao" } )
@EntityScan( basePackages = { "common.entity" } )
@SpringBootApplication
public class RestServiceApp {
    public static void main( String[] args ) {
        SpringApplication.run( RestServiceApp.class );
    }
}
