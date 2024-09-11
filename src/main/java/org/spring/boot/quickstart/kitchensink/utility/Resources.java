package org.spring.boot.quickstart.kitchensink.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resources {

    @Bean
    public Logger produceLog() {
        return LoggerFactory.getLogger("DefaultLogger");
    }
}



