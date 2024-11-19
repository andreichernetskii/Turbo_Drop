package node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories( basePackages = { "common_jpa.dao", "node.dao" } )
@EntityScan( basePackages = { "common_jpa.entity", "node.entity" } )
@SpringBootApplication
public class NodeApp {
    public static void main( String[] args ) {
        SpringApplication.run( NodeApp.class );
    }
}
