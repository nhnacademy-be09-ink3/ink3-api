package shop.ink3.api.common.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class RabbitTemplateConfig {
    @Bean
    @ConditionalOnProperty(name = "rabbit.enabled", havingValue = "true", matchIfMissing = true)
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate();
    }
}

