package shop.ink3.api.coupon.coupon.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;

public interface CouponService {

    // 쿠폰 생성
    CouponResponse createCoupon(CouponCreateRequest coupon);

    // 쿠폰 조회 by id
    CouponResponse getCouponById(long id);

    // 쿠폰 조회 by name
    List<CouponResponse> getCouponByName(String couponName);

    // 모든 쿠폰 조회
    List<CouponResponse> getAllCoupons();

    // 쿠폰 삭제 by 쿠폰 아이디
    void deleteCouponById(Long couponId);

}

