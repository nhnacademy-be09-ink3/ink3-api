package shop.ink3.api.coupon.policy.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;

public record PolicyResponse(
        @NotBlank
        Long policyId,
        @NotBlank
        String policyName,
        @NotBlank
        DiscountType discountType,
        int discount_value,
        @NotBlank
        LocalDateTime validDays,
        @NotBlank
        String message
) {
    public static PolicyResponse from (CouponPolicy policy, String message) {
        return new PolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getDiscountType(),
                policy.getDiscount_value(),
                policy.getValidDays(),
                message
        );
    }
}
