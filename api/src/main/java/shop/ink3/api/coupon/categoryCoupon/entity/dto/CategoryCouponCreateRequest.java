package shop.ink3.api.coupon.categoryCoupon.entity.dto;

import jakarta.validation.constraints.NotNull;
import shop.ink3.api.book.category.entity.Category;

public record CategoryCouponCreateRequest(@NotNull Category category) {
}
