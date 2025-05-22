package shop.ink3.api.coupon.message;

import java.time.LocalDateTime;
import java.util.List;

public record BirthdayBulkMessage(Long couponId, List<Long> userIds, LocalDateTime issuedDate) {
}
