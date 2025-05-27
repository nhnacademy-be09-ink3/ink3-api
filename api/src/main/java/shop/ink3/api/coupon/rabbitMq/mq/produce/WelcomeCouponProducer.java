package shop.ink3.api.coupon.rabbitMq.mq.produce;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import shop.ink3.api.coupon.rabbitMq.message.WelcomeBulkMessage;

@Service
public class WelcomeCouponProducer {
    private final RabbitTemplate rabbitTemplate;

    public WelcomeCouponProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendWelcomeMessage(WelcomeBulkMessage message) {

        rabbitTemplate.convertAndSend(
                "coupon.welcome",
                "coupon.issue.welcome",
                message
        );
    }
}
