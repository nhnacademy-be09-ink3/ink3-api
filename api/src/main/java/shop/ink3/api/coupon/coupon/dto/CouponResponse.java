package shop.ink3.api.coupon.coupon.dto;

import java.time.LocalDateTime;
import java.util.List;
import shop.ink3.api.coupon.coupon.entity.Coupon;

public record CouponResponse(
        Long couponId,
        Long policyId,
        String name,
        LocalDateTime issuableFrom,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        List<BookInfo> books,
        List<CategoryInfo> categories
) {
    public static CouponResponse from(Coupon coupon,
                                      List<BookInfo> books,
                                      List<CategoryInfo> categories) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCouponPolicy().getId(),
                coupon.getName(),
                coupon.getIssuableFrom(),
                coupon.getExpiresAt(),
                coupon.getCreatedAt(),
                books == null ? List.of() : books,
                categories == null ? List.of() : categories
        );
    }

    public record BookInfo(Long originId, Long id, String title) {}
    public record CategoryInfo(Long originId, Long id, String name) {}
}
