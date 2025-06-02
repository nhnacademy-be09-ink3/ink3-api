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

    /****
     * Retrieves all CouponStore entities associated with the specified user ID, eagerly fetching the related coupon entity.
     *
     * @param userId the ID of the user whose CouponStore records are to be retrieved
     * @return a list of CouponStore entities linked to the given user ID
     */
    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserId(Long userId);

    /****
     * Retrieves all CouponStore entities associated with the specified coupon ID, eagerly fetching the related coupon entity.
     *
     * @param couponId the ID of the coupon to filter by
     * @return a list of CouponStore entities linked to the given coupon ID
     */
    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByCouponId(Long couponId);

    /****
     * Retrieves a list of CouponStore entities for a given user ID and coupon status, eagerly fetching the associated coupon entity.
     *
     * @param userId the ID of the user
     * @param status the status of the coupon
     * @return a list of CouponStore entities matching the user ID and status
     */
    @EntityGraph(attributePaths = {"coupon"})
    List<CouponStore> findByUserIdAndStatus(Long userId, CouponStatus status);

    /****
 * Checks if a CouponStore exists for the given user ID, coupon ID, origin type, and origin ID.
 *
 * @param userId the ID of the user
 * @param couponId the ID of the coupon
 * @param originType the type of the origin associated with the coupon
 * @param originId the ID of the origin entity
 * @return true if a matching CouponStore exists, false otherwise
 */
boolean existsByUserIdAndCouponIdAndOriginTypeAndOriginId(Long userId, Long couponId, OriginType originType, long originId);

    /**
     * Checks if a CouponStore exists for the given user ID, coupon ID, and origin type, with a null origin ID.
     *
     * @param userId the ID of the user
     * @param couponId the ID of the coupon
     * @param originType the type of the origin
     * @return true if a matching CouponStore exists with a null origin ID, false otherwise
     */
    boolean existsByUserIdAndCouponIdAndOriginTypeAndOriginIdIsNull(
            Long userId, Long couponId, OriginType originType
    );

    /****
 * Retrieves a list of CouponStore entities for a given user, origin type, a set of origin IDs, and coupon status.
 *
 * @param user_id the ID of the user
 * @param originType the type of the origin associated with the coupon
 * @param originIds a list of origin IDs to filter by
 * @param status the status of the coupon
 * @return a list of matching CouponStore entities
 */
List<CouponStore> findByUserIdAndOriginTypeAndOriginIdInAndStatus(Long user_id, OriginType originType, List<Long> originIds, CouponStatus status);

    /****
 * Retrieves a list of CouponStore entities for a given user, origin type, and coupon status.
 *
 * @param userId the ID of the user
 * @param originType the type of origin associated with the coupon
 * @param status the status of the coupon
 * @return a list of CouponStore entities matching the specified criteria
 */
List<CouponStore> findByUserIdAndOriginTypeAndStatus(Long userId, OriginType originType, CouponStatus status);

    /****
 * Retrieves a `CouponStore` entity matching the specified origin ID and origin type.
 *
 * @param originId the unique identifier of the origin
 * @param originType the type of the origin associated with the coupon store
 * @return the `CouponStore` entity matching the given origin ID and type, or `null` if none found
 */
CouponStore getByOriginIdAndOriginType(Long originId, OriginType originType);
}
