package shop.ink3.api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@RequiredArgsConstructor
public class RabbitTemplateConfig {
    private final JacksonConfig jacksonConfig;
    /**
     * Creates and configures a {@link RabbitTemplate} bean with a JSON message converter.
     *
     * <p>This bean is only created if the property {@code rabbit.enabled} is set to {@code true} or is missing.
     * The {@link RabbitTemplate} is configured to use a Jackson-based JSON message converter provided by the injected {@link JacksonConfig}.</p>
     *
     * @param connectionFactory the connection factory used to create the RabbitTemplate
     * @return a configured RabbitTemplate instance with JSON message conversion
     */
    @Bean
    @ConditionalOnProperty(name = "rabbit.enabled", havingValue = "true", matchIfMissing = true)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonConfig.jackson2JsonMessageConverter());
        return template;
    }
}

