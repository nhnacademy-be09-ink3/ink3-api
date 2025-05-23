package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import shop.ink3.api.coupon.store.entity.CouponStatus;

public record CouponStoreUpdateResponse(
        Long storeId,
        CouponStatus status,
        LocalDateTime usedAt,
        String message
) {
    public static CouponStoreUpdateResponse of(
            shop.ink3.api.coupon.store.entity.CouponStore cs
    ) {
        return new CouponStoreUpdateResponse(
                cs.getId(),
                cs.getStatus(),
                cs.getUsedAt(),
                String.format("CouponStore 엔트리 %d 업데이트 완료", cs.getId())
        );
    }
}
