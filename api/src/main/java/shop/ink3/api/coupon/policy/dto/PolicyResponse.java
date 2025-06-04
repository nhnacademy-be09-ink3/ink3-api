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

        @NotBlank
        int minimumOrderAmount,

        @NotNull
        DiscountType discountType,

        Integer discountValue,

        Integer discountPercentage,

        Integer maximumDiscountAmount,

        LocalDateTime createdAt
) {
    public static PolicyResponse from (CouponPolicy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getMinimumOrderAmount(),
                policy.getDiscountType(),
                policy.getDiscountValue(),
                policy.getDiscountPercentage(),
                policy.getMaximumDiscountAmount(),
                policy.getCreatedAt()
        );
    }
}
