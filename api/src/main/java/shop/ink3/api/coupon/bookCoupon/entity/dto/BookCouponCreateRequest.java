package shop.ink3.api.coupon.bookCoupon.entity.dto;

import jakarta.validation.constraints.NotNull;
import shop.ink3.api.book.book.entity.Book;

public record BookCouponCreateRequest(@NotNull Book book) {
}
