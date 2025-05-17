package shop.ink3.api.coupon.store.dto;

public record CouponStoreDeleteResponse(
        Long storeId,
        String message
) {
    public static CouponStoreDeleteResponse of(Long storeId) {
        return new CouponStoreDeleteResponse(
                storeId,
                String.format("CouponStore 엔트리 %d 삭제 완료", storeId)
        );
    }
}
