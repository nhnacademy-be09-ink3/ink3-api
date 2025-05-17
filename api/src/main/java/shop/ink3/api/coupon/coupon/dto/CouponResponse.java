package shop.ink3.api.coupon.coupon.dto;

import java.time.LocalDateTime;
import java.util.List;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public record CouponResponse(
        Long couponId,
        Long policyId,
        String couponName,
        TriggerType triggerType,
        IssueType issueType,
        String couponCode,
        LocalDateTime issuedAt,
        LocalDateTime expirationDate,
        List<Long> books,
        List<Long> categories
) {
    public static CouponResponse from(Coupon coupon,
                                      List<Long> books,
                                      List<Long> categories) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCouponPolicy().getId(),
                coupon.getCouponName(),
                coupon.getTriggerType(),
                coupon.getIssueType(),
                coupon.getCouponCode(),
                coupon.getIssueDate(),
                coupon.getExpiredDate(),
                books == null ? List.of() : books,
                categories == null ? List.of() : categories
        );
    }
}

