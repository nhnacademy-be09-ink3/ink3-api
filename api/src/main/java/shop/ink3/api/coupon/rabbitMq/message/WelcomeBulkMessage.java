package shop.ink3.api.coupon.rabbitMq.message;

import java.time.LocalDateTime;
import java.util.List;

public record WelcomeBulkMessage(Long couponId, List<Long> userIds, LocalDateTime issuedDate){
}
