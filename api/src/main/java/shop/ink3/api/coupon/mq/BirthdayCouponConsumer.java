//package shop.ink3.api.coupon.mq;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//import shop.ink3.api.coupon.coupon.service.CouponService;
//import shop.ink3.api.coupon.message.BirthdayBulkMessage;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class BirthdayCouponConsumer {
//
//    private final ObjectMapper objectMapper;
//    private final CouponService couponService;
//
//    @RabbitListener(queues = "coupon.birthday.bulk.queue")
//    public void consumeBulk(String payload){
//        try{
//            BirthdayBulkMessage message = objectMapper.readValue(payload, BirthdayBulkMessage.class);
//
//            List<Long> issuedUserIds = couponService.issueBirthdayCoupons(
//                    message.getUserIds(),
//                    message.getCouponId(),
//                    message.getIssuedDate()
//            );
//
//        }catch (Exception e){
//            log.error("Failed to parse bulk message", e);
//        }
//    }
//}
