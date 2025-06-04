package shop.ink3.api.coupon.policy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import shop.ink3.api.coupon.policy.dto.valid.ValidDiscountPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;

@ValidDiscountPolicy
public record PolicyUpdateRequest(
        @NotBlank
        @Size(min = 1, max = 20)
        String name,
        @NotNull
        DiscountType discountType,
        @NotNull @Min(0)
        int minimumOrderAmount,

        int discountValue,

        int discountPercentage,

        int maximumDiscountAmount

) {
}
