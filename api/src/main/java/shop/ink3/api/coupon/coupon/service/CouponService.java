package shop.ink3.api.coupon.coupon.service;

import shop.ink3.api.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;

public interface CouponService {

    // 쿠폰 생성
    CouponResponse createCoupon(CouponCreateRequest coupon);

    // 쿠폰 조회 by id
    CouponResponse getCouponById(long id);

    // 모든 쿠폰 조회
    PageResponse<CouponResponse> getAllCoupons(Pageable pageable);

    // bookId 쿠폰 조회
    PageResponse<CouponResponse> getCouponsByBookId(long id, Pageable pageable);

    // categoryId 쿠폰 조회
    PageResponse<CouponResponse> getCouponsByCategoryId(long id, Pageable pageable);

    // 쿠폰 수정
    CouponResponse updateCoupon(Long couponId, CouponUpdateRequest req);

    // 쿠폰 삭제 by 쿠폰 아이디
    void deleteCouponById(Long couponId);

}

