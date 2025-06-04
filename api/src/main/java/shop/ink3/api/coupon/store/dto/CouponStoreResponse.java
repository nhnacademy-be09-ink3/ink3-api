package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import org.antlr.v4.runtime.atn.SemanticContext.OR;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.OriginType;

@Builder
public record CouponStoreResponse(
        Long storeId,
        Long userId,
        String userName,
        Long couponId,
        String couponName,
        OriginType originType,
        Long originId,
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
                cs.getOriginType(),
                cs.getOriginId(),
                cs.getStatus(),
                cs.getIssuedAt()
        );
    }
}

