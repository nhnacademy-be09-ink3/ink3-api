package shop.ink3.api.coupon.policy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import shop.ink3.api.coupon.policy.dto.valid.ValidDiscountPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;

@ValidDiscountPolicy
public record PolicyCreateRequest(
        @NotBlank
        @Size(min = 1, max = 20)
        String name,
        @NotNull
        DiscountType discountType,
        @NotNull @Min(0)
        int minimum_order_amount,

        int discount_value,

        int discount_percentage,

        int maximum_discount_amount,

        @NotNull
        LocalDateTime valid_days
) {
}
