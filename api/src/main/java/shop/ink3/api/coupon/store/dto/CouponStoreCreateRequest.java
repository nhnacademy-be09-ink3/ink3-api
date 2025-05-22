package shop.ink3.api.coupon.store.dto;

import jakarta.validation.constraints.NotNull;

public record CouponStoreCreateRequest(
        @NotNull(message = "userId는 필수입니다.") Long userId,
        @NotNull(message = "couponId는 필수입니다.") Long couponId
) {}
