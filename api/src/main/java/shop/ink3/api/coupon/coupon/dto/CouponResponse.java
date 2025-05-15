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
        LocalDateTime expirationDate,
        List<BookInfo> books,
        List<CategoryInfo> categories
) {
    public record BookInfo(Long bookId, String bookName) {}
    public record CategoryInfo(Long categoryId, String categoryName) {}
    public static CouponResponse from(Coupon coupon,
                                      List<BookCoupon> bookCoupons,
                                      List<CategoryCoupon> categoryCoupons) {

        List<BookInfo> books = bookCoupons.stream()
                .map(bc -> new BookInfo(bc.getBook().getId(), bc.getBook().getTitle()))
                .toList();

        List<CategoryInfo> categories = categoryCoupons.stream()
                .map(cc -> new CategoryInfo(cc.getCategory().getId(), cc.getCategory().getName()))
                .toList();

        return new CouponResponse(
                coupon.getId(),
                coupon.getCouponPolicy().getId(),
                coupon.getCouponName(),
                coupon.getTriggerType(),
                coupon.getIssueType(),
                coupon.getCouponCode(),
                coupon.getExpiredDate(),
                books.isEmpty() ? null : books,
                categories.isEmpty() ? null : categories
        );
    }
}

