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
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        @Size(min = 1, max = 20, message = "이름은 1~20자여야 합니다.")
        String name,

        @NotNull(message = "할인 타입은 필수입니다.")
        DiscountType discountType,

        @NotNull(message = "최소 주문 금액은 필수입니다.")
        @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다.")
        Integer minimum_order_amount,

        @NotNull(message = "할인 금액은 필수입니다.")
        Integer discount_value,

        @NotNull(message = "할인 비율은 필수입니다.")
        Integer discount_percentage,

        @NotNull(message = "최대 할인 금액은 필수입니다.")
        @Min(value = 0, message = "최대 할인 금액은 0 이상이어야 합니다.")
        Integer maximum_discount_amount,

        @NotNull(message = "생성일자는 필수입니다.")
        LocalDateTime createdAt
) {}