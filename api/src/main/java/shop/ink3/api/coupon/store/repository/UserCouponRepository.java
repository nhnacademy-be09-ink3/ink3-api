package shop.ink3.api.coupon.store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.coupon.store.entity.CouponStore;

public interface UserCouponRepository extends JpaRepository<CouponStore, Long> {

    // 특정 유저의 전체 쿠폰
    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserId(Long userId);
    // 특정 유저의 미사용 쿠폰만
    List<CouponStore> findByUserIdAndIsUsedFalse(Long userId);

    // 유저가 특정 쿠폰을 이미 가지고 있는지 여부
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    // 유저의 특정 쿠폰 조회
    Optional<CouponStore> findByUserIdAndCouponId(Long userId, Long couponId);
}
