package shop.ink3.api.coupon.bookCoupon.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.coupon.coupon.entity.Coupon;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {

    /****
     * Retrieves the IDs of all BookCoupon entities associated with the specified book ID.
     *
     * @param bookId the ID of the book whose associated BookCoupon IDs are to be retrieved
     * @return a list of BookCoupon IDs linked to the given book ID
     */
    @Query("select bc.id from BookCoupon bc where bc.book.id = :bookId")
    List<Long> findIdsByBookId(@Param("bookId") Long bookId);

    /****
 * Retrieves a BookCoupon entity matching the specified coupon ID and book ID.
 *
 * @param couponId the ID of the coupon to match
 * @param bookId the ID of the book to match
 * @return the BookCoupon entity with the given coupon and book IDs, or null if none found
 */
BookCoupon getByCouponIdAndBookId(Long couponId, Long bookId);
}

