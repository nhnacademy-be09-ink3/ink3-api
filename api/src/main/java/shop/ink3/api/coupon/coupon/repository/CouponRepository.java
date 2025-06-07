package shop.ink3.api.coupon.coupon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 쿠폰 id로 쿠폰 상세 조회
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
    List<Coupon> findAllByNameWithFetch(@Param("name") String name);


    // 모든 쿠폰 조회
    @Query("SELECT DISTINCT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category")
    Page<Coupon> findAllWithAssociations(Pageable pageable);

    // 북 id로 쿠폰 조회
    List<Coupon> getCouponsByBookCoupons_BookId(Long id);

    // 카테고리 id로 쿠폰 조회
    List<Coupon> getCouponsByCategoryCoupons_CategoryId(Long id);


}

