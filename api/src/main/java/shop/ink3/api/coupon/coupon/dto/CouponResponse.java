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
    /**
     * Creates a {@code CouponResponse} from a {@code Coupon} entity and optional lists of associated books and categories.
     *
     * If the {@code books} or {@code categories} lists are {@code null}, they will be set to empty lists in the response.
     *
     * @param coupon      the coupon entity to convert
     * @param books       the list of associated book information, or {@code null}
     * @param categories  the list of associated category information, or {@code null}
     * @return a {@code CouponResponse} representing the coupon and its related books and categories
     */
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
