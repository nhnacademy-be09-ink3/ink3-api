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


    boolean existsByUserIdAndOriginType(Long userId, OriginType originType);

    boolean existsByUserIdAndCouponIdAndOriginTypeAndOriginId(Long userId, Long couponId, OriginType originType, Long originId);

    List<CouponStore> findByUserIdAndOriginTypeAndOriginIdInAndStatus(Long user_id, OriginType originType, List<Long> originIds, CouponStatus status);

    List<CouponStore> findByUserIdAndOriginTypeAndStatus(Long userId, OriginType originType, CouponStatus status);

    @Query("""
        SELECT cs
        FROM CouponStore cs
        JOIN FETCH cs.coupon c
        WHERE cs.user.id = :userId
          AND cs.originType = :originType
          AND cs.originId IN :originIds
          AND cs.status = :status
    """)
    List<CouponStore> findWithCouponByUserAndOriginAndStatus(
            @Param("userId") Long userId,
            @Param("originType") OriginType originType,
            @Param("originIds") Collection<Long> originIds,
            @Param("status") CouponStatus status
    );

    @Query("""
        SELECT cs
        FROM CouponStore cs
        JOIN FETCH cs.coupon c
        WHERE cs.user.id = :userId
          AND cs.originType = :originType
          AND cs.status = :status
    """)
    List<CouponStore> findWithCouponByUserAndOriginAndStatus(
            @Param("userId") Long userId,
            @Param("originType") OriginType originType,
            @Param("status") CouponStatus status
    );
}
