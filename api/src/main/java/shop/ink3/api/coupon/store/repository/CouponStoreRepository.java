package shop.ink3.api.coupon.store.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.store.entity.CouponStatus;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.coupon.store.entity.OriginType;
import shop.ink3.api.user.user.entity.User;

public interface CouponStoreRepository extends JpaRepository<CouponStore, Long> {

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByCouponId(Long couponId);

    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserIdAndStatus(Long userId, CouponStatus status);

    boolean existsByUserIdAndCouponIdAndOriginTypeAndOriginId(Long userId, Long couponId, OriginType originType, long originId);

    boolean existsByUserIdAndCouponIdAndOriginTypeAndOriginIdIsNull(
            Long userId, Long couponId, OriginType originType
    );

    List<CouponStore> findByUserIdAndOriginTypeAndOriginIdInAndStatus(Long user_id, OriginType originType, List<Long> originIds, CouponStatus status);

    List<CouponStore> findByUserIdAndOriginTypeAndStatus(Long userId, OriginType originType, CouponStatus status);

    CouponStore getByOriginIdAndOriginType(Long originId, OriginType originType);
}
