package shop.ink3.api.coupon.coupon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 특정 트리거 타입에 해당하는 모든 쿠폰 조회
    List<Coupon> findByTriggerType(TriggerType triggerType);

    // 특정 issue 타입에 해당하는 모든 쿠폰 조회
    List<Coupon> findByIssueType(IssueType issueType);

//    // 트리거 + 특정 책에 연결된 쿠폰들 (JOIN 필요할 경우 직접 JPQL 사용)
//    @Query("SELECT c FROM Coupon c JOIN BookCoupon bc ON c.id = bc.couponId WHERE bc.bookId = :bookId AND c.triggerType = :triggerType")
//    List<Coupon> findBookCouponsByTrigger(@Param("bookId") Long bookId, @Param("triggerType") TriggerType triggerType);
//
//    // 카테고리 트리거 조건 쿠폰
//    @Query("SELECT c FROM Coupon c JOIN CategoryCoupon cc ON c.id = cc.couponId WHERE cc.categoryId = :categoryId AND c.triggerType = :triggerType")
//    List<Coupon> findCategoryCouponsByTrigger(@Param("categoryId") Long categoryId, @Param("triggerType") TriggerType triggerType);

    // 코드 기반 쿠폰 조회
    Optional<Coupon> findByCouponCode(String couponCode);

    // 쿠폰 이름으로 쿠폰 조회
    Optional<Coupon> findByCouponName(String couponName);
}

