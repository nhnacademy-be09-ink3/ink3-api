package shop.ink3.api.coupon.mq;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import shop.ink3.api.common.config.RabbitConfig;
import shop.ink3.api.coupon.message.WelcomeBulkMessage;

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
