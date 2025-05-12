package shop.ink3.api.coupon.policy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import shop.ink3.api.coupon.policy.entity.DiscountType;

public record PolicyCreateRequest(
        @NotBlank
        @Size(min = 1, max = 20)
        String name,
        @NotBlank
        DiscountType discountType,
        @NotBlank
        int minimum_order_amount,

        int discount_value,

        int maximum_discount_amount,

        @NotBlank
        LocalDateTime valid_days
) {
}
