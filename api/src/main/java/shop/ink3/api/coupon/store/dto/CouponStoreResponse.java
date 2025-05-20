package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.store.entity.CouponStatus;

@Builder
public record CouponStoreResponse(
        Long storeId,
        Long userId,
        Long couponId,
        LocalDateTime createdAt,
        CouponStatus couponStatus,
        LocalDateTime usedAt
) {
    public static CouponStoreResponse fromEntity(shop.ink3.api.coupon.store.entity.CouponStore cs) {
        return new CouponStoreResponse(
                cs.getId(),
                cs.getUser().getId(),
                cs.getCoupon().getId(),
                cs.getCreatedAt(),
                cs.getCouponStatus(),
                cs.getUsedAt()
        );
    }
}

