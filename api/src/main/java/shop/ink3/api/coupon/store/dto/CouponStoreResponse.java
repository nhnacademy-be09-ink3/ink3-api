package shop.ink3.api.coupon.store.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponStoreResponse {
    private Long id;
    private String couponName;
    private String couponCode;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean isUsed;
}

