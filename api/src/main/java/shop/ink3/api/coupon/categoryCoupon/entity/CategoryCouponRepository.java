package shop.ink3.api.coupon.categoryCoupon.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
    List<CategoryCoupon> findByCategoryId(Long categoryId);
    List<CategoryCoupon> findByCouponId(Long couponId);
}
