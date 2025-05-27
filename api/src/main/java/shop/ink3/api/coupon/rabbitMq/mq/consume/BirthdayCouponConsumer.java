package shop.ink3.api.coupon.rabbitMq.mq.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.ink3.api.coupon.coupon.service.CouponService;
import shop.ink3.api.coupon.rabbitMq.message.BirthdayCouponMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayCouponConsumer {

    private final ObjectMapper objectMapper;
    private final CouponService couponService;

    @RabbitListener(queues = "coupon.birthday")
    public void consume(byte[] body, @Headers Map<String, Object> headers) {
        try {
            String type = (String) headers.get("type");
            Object message = deserialize(type, body);

            if (message instanceof BirthdayCouponMessage birthdayCoupon) {
                log.info("🎂 생일 쿠폰 수신: userIds={}, couponId={}, issuedDate={}",
                        birthdayCoupon.getUserIds(), birthdayCoupon.getCouponId(), birthdayCoupon.getIssuedDate());
                processCouponIssue(birthdayCoupon);
            } else {
                log.warn("⚠️ 알 수 없는 메시지 타입: {}", type);
            }
        } catch (Exception e) {
            log.error("❌ 메시지 처리 실패", e);
            throw new AmqpRejectAndDontRequeueException("DLQ로 보냅니다.");
        }
    }

    private Object deserialize(String type, byte[] body) throws IOException {
        return switch (type) {
            case "BirthdayCouponMessage" -> objectMapper.readValue(body, BirthdayCouponMessage.class);
//            case "WelcomeCouponMessage" -> objectMapper.readValue(body, WelcomeCouponMessage.class);
            // 필요 시 case 추가
            default -> null;
        };
    }

    // 비동기 분산 처리
    @Async
    public void processCouponIssue(BirthdayCouponMessage birthdayCoupon){
        couponService.issueBirthdayCoupons(birthdayCoupon.getUserIds(), birthdayCoupon.getCouponId(), birthdayCoupon.getIssuedDate());
    }
}

