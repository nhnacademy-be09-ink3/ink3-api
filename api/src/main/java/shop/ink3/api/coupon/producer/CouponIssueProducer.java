package shop.ink3.api.coupon.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import shop.ink3.api.coupon.coupon.dto.CouponIssueRequest;

@Component
@RequiredArgsConstructor
public class CouponIssueProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendCouponIssueMessage(CouponIssueRequest request) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY,
                request
        );
    }
}

