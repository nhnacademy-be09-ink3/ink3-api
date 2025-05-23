package shop.ink3.api.coupon.store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;

public interface UserCouponRepository extends JpaRepository<CouponStore, Long> {

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByCouponId(Long couponId);

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserIdAndStatus(Long userId, CouponStatus status);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @Query("SELECT COUNT(c) > 0 FROM CouponStore c WHERE c.user.id = :userId AND c.coupon.id = :couponId AND FUNCTION('YEAR', c.issuedAt) = :year")
    boolean existsByUserIdAndCouponIdAndYear(@Param("userId") Long userId,
                                             @Param("couponId") Long couponId,
                                             @Param("year") int year);

}
