package shop.ink3.api.coupon.coupon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.coupon.entity.IssueType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 특정 issue 타입에 해당하는 모든 쿠폰 조회
    @Query("SELECT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category " +
            "WHERE c.issueType = :issueType")
    Optional<List<Coupon>> findAllByIssueTypeWithFetch(@Param("issueType") IssueType issueType);

    @Query("SELECT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category " +
            "WHERE c.id = :id")
    Optional<Coupon> findByIdWithFetch(@Param("id") Long id);

    void deleteByName(String couponName);

    // 쿠폰 이름으로 쿠폰 조회
    @Query("SELECT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category " +
            "WHERE c.name = :name")
    Optional<List<Coupon>> findAllByNameWithFetch(@Param("name") String name);



    @Query("SELECT DISTINCT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category")
    List<Coupon> findAllWithAssociations();

}

