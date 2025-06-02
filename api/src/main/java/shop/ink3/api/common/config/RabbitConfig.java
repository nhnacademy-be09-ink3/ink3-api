package shop.ink3.api.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*

[Producer]
    ↓  RabbitTemplate.convertAndSend()
[Exchange: coupon.exchange]
    ↓ (routingKey = coupon.routing)
[Queue: coupon.queue]
    ↓
[Consumer (@RabbitListener)]

*/

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "coupon.exchange";

    /**
     * Creates a durable queue for storing welcome coupon messages.
     *
     * The queue is named "coupon.welcome" and persists across server restarts.
     * Messages sent to this queue are later consumed by RabbitMQ listeners.
     *
     * @return a durable Queue instance for welcome coupons
     */
    @Bean
    public Queue welcomeQueue() {
        return new Queue("coupon.welcome", true);
    }

    /**
     * Creates a durable queue for birthday coupon messages with dead-lettering enabled.
     *
     * Messages that cannot be processed are redirected to the "dlx.exchange" exchange using the "dlx.coupon" routing key.
     *
     * @return a durable Queue instance for birthday coupons with dead-letter configuration
     */
    @Bean
    public Queue birthdayQueue() {
        return QueueBuilder.durable("coupon.birthday")
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key","dlx.coupon")
                .build();
    }

    /****
     * Defines a durable dead-letter queue for birthday coupon messages that cannot be processed.
     *
     * @return a durable queue named "coupon.birthday.dead"
     */
    @Bean
    public Queue birthdayQueueDead(){ return new Queue("coupon.birthday.dead", true); }

    /**
     * Defines the main topic exchange for routing coupon-related messages.
     *
     * Creates a topic exchange named "coupon.exchange" to enable flexible routing of messages to queues based on routing keys.
     *
     * @return the configured TopicExchange for coupon messaging
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    /****
     * Defines a topic exchange named "dlx.exchange" for routing dead-lettered messages.
     *
     * @return the dead-letter topic exchange
     */
    @Bean
    public TopicExchange dlxExchange() {
        return new TopicExchange("dlx.exchange");
    }

    /**
     * Binds the welcome coupon queue to the coupon exchange using the "coupon.issue.welcome" routing key.
     *
     * Messages sent to the "coupon.exchange" exchange with this routing key will be routed to the "coupon.welcome" queue.
     *
     * @return the binding between the welcome queue and the exchange
     */

    @Bean
    public Binding bindWelcomeQueue() {
        return BindingBuilder.bind(welcomeQueue()).to(exchange()).with("coupon.issue.welcome");
    }

    /****
     * Binds the birthday coupon queue to the main coupon exchange with the routing key "coupon.birthday".
     *
     * @return the binding between the "coupon.birthday" queue and the "coupon.exchange" exchange
     */
    @Bean
    public Binding bindBirthdayQueue() {
        return BindingBuilder.bind(birthdayQueue()).to(exchange()).with("coupon.birthday");
    }

    /**
     * Creates a binding between the dead-letter queue for birthday coupons and the dead-letter exchange using the routing key "dlx.coupon".
     *
     * This ensures that messages dead-lettered from the birthday coupon queue are routed to the appropriate dead-letter queue.
     *
     * @return the binding for the birthday coupon dead-letter queue
     */
    @Bean
    public Binding bindBirthdayDLQ() {
        return BindingBuilder.bind(birthdayQueueDead()).to(dlxExchange()).with("dlx.coupon");
    }
    /*
     message를 자동으로 Json <-> java객체로 직렬화/역직렬화 해주는 변환기
     RebbitTemplate 및 @RabbitListener에서 DTO객체를 바로 주고받을 수 있게 해줌
    */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

