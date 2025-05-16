package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;

public record CouponStoreResponse(
        Long storeId,
        Long userId,
        Long couponId,
        LocalDateTime createdAt,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        boolean isUsed,
        LocalDateTime usedAt
) {
    public static CouponStoreResponse fromEntity(shop.ink3.api.coupon.store.entity.CouponStore cs) {
        return new CouponStoreResponse(
                cs.getId(),
                cs.getUser().getId(),
                cs.getCoupon().getId(),
                cs.getCreatedAt(),
                cs.getValidFrom(),
                cs.getValidUntil(),
                cs.isUsed(),
                cs.getUsedAt()
        );
    }
}

