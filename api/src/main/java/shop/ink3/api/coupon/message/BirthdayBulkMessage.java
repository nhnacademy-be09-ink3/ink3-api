package shop.ink3.api.coupon.message;

import java.time.LocalDateTime;
import java.util.List;

public record BirthdayBulkMessage(Long couponId, LocalDateTime issuedDate, String trigger, List<Long> userIds) {
}
