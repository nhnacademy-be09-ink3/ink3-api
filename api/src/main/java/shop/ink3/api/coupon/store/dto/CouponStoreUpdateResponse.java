package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;

public record CouponStoreUpdateResponse(
        Long storeId,
        boolean isUsed,
        LocalDateTime usedAt,
        String message
) {
    public static CouponStoreUpdateResponse of(
            shop.ink3.api.coupon.store.entity.CouponStore cs
    ) {
        return new CouponStoreUpdateResponse(
                cs.getId(),
                cs.isUsed(),
                cs.getUsedAt(),
                String.format("CouponStore 엔트리 %d 업데이트 완료", cs.getId())
        );
    }
}
