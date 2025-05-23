package shop.ink3.api.coupon.store.exception;

public class CouponStoreNotFoundException extends RuntimeException {
    public CouponStoreNotFoundException(String message) {
        super(message);
    }
}
