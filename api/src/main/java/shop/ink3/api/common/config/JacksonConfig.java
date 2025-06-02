package shop.ink3.api.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    /****
     * Creates a Jackson2JsonMessageConverter bean with support for Java 8 date and time types.
     *
     * @return a Jackson2JsonMessageConverter configured with an ObjectMapper that handles Java time types such as LocalDate
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // LocalDate 처리용
        return new Jackson2JsonMessageConverter(mapper);
    }
}
