package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.OriginType;

public record CouponStoreDto(
        Long storeId,
        Long couponId,
        String couponName,
        LocalDateTime expiresAt,
        OriginType originType,
        Long originId,
        CouponStatus status,
        DiscountType discountType,
        Integer discountValue,
        Integer discountPercentage,
        Integer maximumDiscountAmount
) {}
