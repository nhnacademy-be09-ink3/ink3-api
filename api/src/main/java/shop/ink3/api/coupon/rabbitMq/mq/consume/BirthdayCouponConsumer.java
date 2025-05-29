package shop.ink3.api.coupon.rabbitMq.mq.consume;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.service.Impl.CouponServiceImpl;
import shop.ink3.api.coupon.message.BirthdayBulkMessage;
import shop.ink3.api.coupon.store.dto.CouponIssueRequest;
import shop.ink3.api.coupon.store.entity.OriginType;
import shop.ink3.api.coupon.store.service.CouponStoreService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayCouponConsumer {

    private final ObjectMapper objectMapper;
    private final CouponServiceImpl couponService;
    private final CouponStoreService couponStoreService;

    @Async
    @RabbitListener(queues = "coupon.birthday", concurrency = "3")
    public void consumeBulk(String payload){
        try{
            BirthdayBulkMessage message = objectMapper.readValue(payload, BirthdayBulkMessage.class);
            System.out.println(message.userIds());
            CouponCreateRequest couponCreateRequest = new CouponCreateRequest(1L, "BIRTHDAY", LocalDateTime.now(), LocalDateTime.now().plusDays(30), null, null);
            CouponResponse coupon = couponService.createCoupon(couponCreateRequest);
            Long couponId = coupon.couponId();

            message.userIds().forEach(id ->
                    couponStoreService.issueCoupon(
                            new CouponIssueRequest(id, couponId, OriginType.BIRTHDAY, null)
                    )
            );


        }catch (Exception e){
            log.error("Failed to parse bulk message", e);
        }
    }
}
