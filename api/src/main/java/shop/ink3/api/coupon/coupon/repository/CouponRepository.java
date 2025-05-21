package shop.ink3.api.coupon.coupon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.bookCoupon.entity.BookCoupon;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;
import shop.ink3.api.coupon.coupon.entity.TriggerType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 특정 issue 타입에 해당하는 모든 쿠폰 조회
    Optional<List<Coupon>> findAllByIssueType(IssueType issueType);

    void deleteByName(String couponName);

    // 쿠폰 이름으로 쿠폰 조회
    List<Coupon> findAllByName(String couponName);

}

