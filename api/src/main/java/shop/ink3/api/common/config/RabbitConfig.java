package shop.ink3.api.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
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

    public static final String QUEUE_NAME = "coupon.queue";
    public static final String EXCHANGE_NAME = "coupon.exchange";
    public static final String ROUTING_KEY = "coupon.routing";

    /*
     coupon을 담을 queue 생성
     durable = true : 서버를 재시작해도 큐가 (영구적으로) 지속됨
     message는 이 큐에 쌓이고, 이후 @RabbitListener에서 소비됨.
    */
    @Bean
    public Queue testQueue() {
        return new Queue(QUEUE_NAME, true); // durable
    }

    @Bean
    public Queue welcomeQueue() {
        return new Queue("coupon.welcome", true);
    }

    @Bean
    public Queue birthdayQueue() {
        return new Queue("coupon.birthday", true);
    }

    @Bean
    public Queue bookQueue() {
        return new Queue("coupon.book", true);
    }

    @Bean
    public Queue categoryQueue() {
        return new Queue("coupon.category", true);
    }

    /*
     message를 어디로 보낼지 라우팅해주는 교환기 생성
     Topic 타입은 "coupon.* -> coupon.routing, coupon.created : 모두 수신 가능
                 "coupon.welcome : 특정 type에서 수신
    */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    /*
     특정 Routing key를 통해 message를 Exchange -> Queue로 연결
     즉, "coupon.routing" 키를 가진 message가 오면 coupon.queue로 전달
     이걸 해줘야 message가 queue로 들어가게 됨
    */
    @Bean
    public Binding binding(Queue testQueue, TopicExchange exchange) {
        return BindingBuilder.bind(testQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindWelcomeQueue() {
        return BindingBuilder.bind(welcomeQueue()).to(exchange()).with("coupon.issue.welcome");
    }

    @Bean
    public Binding bindBirthdayQueue() {
        return BindingBuilder.bind(birthdayQueue()).to(exchange()).with("coupon.birthday.bulk");
    }

    @Bean
    public Binding bindBookQueue() {
        return BindingBuilder.bind(bookQueue()).to(exchange()).with("coupon.issue.book");
    }

    @Bean
    public Binding bindCategoryQueue() {
        return BindingBuilder.bind(categoryQueue()).to(exchange()).with("coupon.category");
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

