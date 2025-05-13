package shop.ink3.api.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.order.cart.dto.CartResponse;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, List<CartResponse>> redisCartTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, List<CartResponse>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
