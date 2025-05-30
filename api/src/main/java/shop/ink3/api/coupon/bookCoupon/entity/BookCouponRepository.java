package shop.ink3.api.coupon.bookCoupon.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.coupon.coupon.entity.Coupon;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {

    @Query("select bc.id from BookCoupon bc where bc.book.id = :bookId")
    List<Long> findIdsByBookId(@Param("bookId") Long bookId);

    BookCoupon getByCouponIdAndBookId(Long couponId, Long bookId);
}

