package shop.ink3.api.coupon.rabbitMq.message;

import java.util.List;

public record BirthdayCouponMessage(List<Long> userIds) {
}
