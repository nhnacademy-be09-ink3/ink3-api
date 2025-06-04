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

        int discountValue,

        int discountPercentage,

        LocalDateTime createdAt

) {
    public static PolicyResponse from (CouponPolicy policy) {
        return new PolicyResponse(
                policy.getId(),
                policy.getName(),
                policy.getDiscountType(),
                policy.getDiscountValue(),
                policy.getDiscountPercentage(),
                policy.getCreatedAt()
        );
    }
}
