package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.store.entity.CouponStatus;

@Builder
public record CouponStoreResponse(
        Long storeId,
        Long userId,
        String userName,
        Long couponId,
        String couponName,
        CouponStatus status,
        LocalDateTime issuedAt
) {
    public static CouponStoreResponse fromEntity(shop.ink3.api.coupon.store.entity.CouponStore cs) {
        return new CouponStoreResponse(
                cs.getId(),
                cs.getUser().getId(),
                cs.getUser().getName(),
                cs.getCoupon().getId(),
                cs.getCoupon().getName(),
                cs.getStatus(),
                cs.getIssuedAt()
        );
    }
}

