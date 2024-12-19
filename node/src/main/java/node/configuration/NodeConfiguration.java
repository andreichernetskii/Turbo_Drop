package node.configuration;

import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {

    @Value( "${salt}" )
    private String salt;

    @Bean
    public Hashids getHashids() {
        return new Hashids( salt );
    }
}
