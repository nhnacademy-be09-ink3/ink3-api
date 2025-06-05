package shop.ink3.api.common.config;

import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SomethingApiConfig {

    @Bean
    JsonFormWriter jsonFormWriter() {
        return new JsonFormWriter();

    }
}