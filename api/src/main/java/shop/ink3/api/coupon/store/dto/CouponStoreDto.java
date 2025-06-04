package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.OriginType;

public record CouponStoreDto(
        Long storeId,
        Long couponId,
        String couponName,
        LocalDateTime expiresAt,
        OriginType originType,
        Long originId,
        CouponStatus status
) {}
