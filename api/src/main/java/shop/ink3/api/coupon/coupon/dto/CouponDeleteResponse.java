package shop.ink3.api.coupon.coupon.dto;

public record CouponDeleteResponse(
        Long couponId,
        String couponName,
        String message
) {
    public static CouponDeleteResponse deleteById(Long couponId) {
        return new CouponDeleteResponse(
                couponId,
                null,
                String.format("Deleted couponId %d", couponId)
        );
    }

    public static CouponDeleteResponse deleteByName(String couponName) {
        return new CouponDeleteResponse(
                null,
                couponName,
                String.format("Deleted couponName %s", couponName)
        );
    }
}
