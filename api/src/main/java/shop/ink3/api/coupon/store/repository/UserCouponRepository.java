package shop.ink3.api.coupon.store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.coupon.store.entity.CouponStore;

public interface UserCouponRepository extends JpaRepository<CouponStore, Long> {

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByCouponId(Long couponId);

    // 기존: List<CouponStore> findByUserIdAndUsedFalse(Long userId);
    // 수정:
    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserIdAndIsUsedFalse(Long userId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}
