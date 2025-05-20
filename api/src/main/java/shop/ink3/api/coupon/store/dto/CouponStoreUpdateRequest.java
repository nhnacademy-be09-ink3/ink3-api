package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import shop.ink3.api.coupon.store.entity.CouponStatus;

public record CouponStoreUpdateRequest(
        /** used 상태로만 업데이트할 경우 */
        CouponStatus couponStatus,
        /** isUsed=true 일 때 기록할 사용 시각 */
        LocalDateTime usedAt
) {}
