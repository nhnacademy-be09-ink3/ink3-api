package shop.ink3.api.coupon.coupon.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.categoryCoupon.entity.CategoryCoupon;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;

public interface CouponService {

    // 쿠폰 생성
    CouponResponse createCoupon(CouponCreateRequest coupon);

    // 쿠폰 조회 by id
    CouponResponse getCouponById(long id);

    // 모든 쿠폰 조회
    List<CouponResponse> getAllCoupons();

    // bookId 쿠폰 조회
    List<CouponResponse> getCouponsByBookId(long id);

    // categoryId 쿠폰 조회
    List<CouponResponse> getCouponsByCategoryId(long id);

    // 쿠폰 수정
    CouponResponse updateCoupon(Long couponId, CouponUpdateRequest req);

    // 쿠폰 삭제 by 쿠폰 아이디
    void deleteCouponById(Long couponId);

}

