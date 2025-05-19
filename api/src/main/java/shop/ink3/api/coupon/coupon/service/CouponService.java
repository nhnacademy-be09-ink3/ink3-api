package shop.ink3.api.coupon.coupon.service;

import java.util.List;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;
import shop.ink3.api.coupon.store.dto.CouponStoreResponse;

public interface CouponService {

    // 쿠폰 생성
    CouponResponse createCoupon(CouponCreateRequest coupon);

    // 쿠폰 조회 by 트리거 타입
    List<CouponResponse> getCouponByTriggerType(TriggerType triggerType);

    // 쿠폰 조회 by issue 타입
    List<CouponResponse> getCouponByIssueType(IssueType issueType);

    // 쿠폰 조회 by id
    CouponResponse getCouponById(long id);

    // 쿠폰 조회 by name
    List<CouponResponse> getCouponByCouponName(String couponName);

    // 모든 쿠폰 조회
    List<CouponResponse> getAllCoupons();

    // 쿠폰 삭제 by 쿠폰 아이디
    void deleteCouponById(Long couponId);

    // 쿠폰 삭제 by 쿠폰 이름
    void deleteCouponByName(String couponName);

    // 📘 도서 관련 쿠폰
    void issueBookCoupons(Long userId, Long bookId);

    // 📗 카테고리 관련 쿠폰
    void issueCategoryCoupons(Long userId, Long categoryId);

    // ✋ 쿠폰 코드 입력 발급 (공통)
    void issueCouponByCode(Long userId, String couponCode);

    // 🎁 쿠폰 ID로 직접 발급 (예: 다운로드)
    void issueCouponById(Long userId, Long couponId);

    // 📃 발급 이력 확인 (보관함)
    List<CouponStoreResponse> getUserCoupons(Long userId);

    // ✅ 쿠폰 사용 처리
    void useCoupon(Long userCouponId, String orderId);

    // ♻️ 쿠폰 사용 취소 처리 (선택)
    void cancelCouponUse(Long userCouponId);
}

