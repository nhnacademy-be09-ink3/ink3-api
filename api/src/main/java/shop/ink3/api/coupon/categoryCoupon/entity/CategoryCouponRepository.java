package shop.ink3.api.coupon.categoryCoupon.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {

    /**
     * Retrieves the IDs of all CategoryCoupon entities associated with the specified category.
     *
     * @param categoryId the ID of the category to filter by
     * @return a list of CategoryCoupon IDs linked to the given category
     */
    @Query("select cc.id from CategoryCoupon cc where cc.category.id = :categoryId")
    List<Long> findIdsByCategoryId(@Param("categoryId") Long categoryId);

    /****
 * Retrieves the CategoryCoupon entity matching the specified coupon ID and category ID.
 *
 * @param couponId the ID of the coupon to match
 * @param categoryId the ID of the category to match
 * @return the CategoryCoupon entity with the given coupon and category IDs, or null if not found
 */
CategoryCoupon getByCouponIdAndCategoryId(Long couponId, Long categoryId);
}

