package shop.ink3.api.coupon.store.entity;

public enum CouponStatus {
    AVAILABLE, // 아직 사용자가 선택하지 않은 공개된 쿠폰
    USED, // 사용됨
    EXPIRED // 만료됨 (유효 기간 지남)
}
