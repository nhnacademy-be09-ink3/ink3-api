package shop.ink3.api.coupon.policy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import shop.ink3.api.coupon.policy.dto.valid.ValidDiscountPolicy;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;

@ValidDiscountPolicy
public record PolicyResponse(
        @NotNull
        Long policyId,

        @NotBlank
        String policyName,

        @NotNull
        DiscountType discountType,

        int discount_value,

        int discount_percentage,

        LocalDateTime createdAt,

        @NotBlank
        String message
) {
    public static PolicyResponse from (CouponPolicy policy, String message) {
        return new PolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getDiscountType(),
                policy.getDiscount_value(),
                policy.getDiscount_percentage(),
                policy.getCreatedAt(),
                message
        );
    }
}
