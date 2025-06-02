package shop.ink3.api.coupon.coupon.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.coupon.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * Retrieves a coupon by its ID, eagerly loading associated book and category entities.
     *
     * @param id the ID of the coupon to retrieve
     * @return an Optional containing the coupon with its related books and categories, or empty if not found
     */
    @Query("SELECT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category " +
            "WHERE c.id = :id")
    Optional<Coupon> findByIdWithFetch(@Param("id") Long id);

    void deleteByName(String couponName);

    /**
     * Retrieves all coupons with the specified name, eagerly loading associated books and categories.
     *
     * @param name the name of the coupons to retrieve
     * @return an Optional containing a list of matching coupons with their related books and categories, or an empty Optional if none found
     */
    @Query("SELECT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category " +
            "WHERE c.name = :name")
    Optional<List<Coupon>> findAllByNameWithFetch(@Param("name") String name);


    /**
     * Retrieves all coupons with their associated books and categories eagerly loaded.
     *
     * @return a list of all coupons, each with related book and category entities fetched
     */
    @Query("SELECT DISTINCT c FROM Coupon c " +
            "LEFT JOIN FETCH c.bookCoupons bc " +
            "LEFT JOIN FETCH bc.book " +
            "LEFT JOIN FETCH c.categoryCoupons cc " +
            "LEFT JOIN FETCH cc.category")
    List<Coupon> findAllWithAssociations();

    /****
 * Retrieves all coupons associated with the specified book ID via the book-coupon relationship.
 *
 * @param id the ID of the book
 * @return a list of coupons linked to the given book ID
 */
    List<Coupon> getCouponsByBookCoupons_BookId(Long id);

    /****
 * Retrieves all coupons associated with the specified category ID.
 *
 * @param id the ID of the category
 * @return a list of coupons linked to the given category
 */
    List<Coupon> getCouponsByCategoryCoupons_CategoryId(Long id);


}

