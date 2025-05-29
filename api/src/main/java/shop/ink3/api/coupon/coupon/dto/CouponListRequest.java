package shop.ink3.api.coupon.coupon.dto;

import java.util.List;

public record CouponListRequest(List<Long> bookIds, Long userId) {
}
