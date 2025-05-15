package shop.ink3.api.coupon.coupon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import shop.ink3.api.coupon.bookCoupon.entity.dto.BookCouponCreateRequest;
import shop.ink3.api.coupon.categoryCoupon.entity.dto.CategoryCouponCreateRequest;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponCreateRequest(
        @NotNull Long policyId,
        @NotBlank String couponName,
        @NotNull TriggerType triggerType,
        @NotNull IssueType issueType,
        String CouponCode,
        @NotNull LocalDateTime expirationDate,

        List<BookCouponCreateRequest> books,
        List<CategoryCouponCreateRequest> categories

) { }
