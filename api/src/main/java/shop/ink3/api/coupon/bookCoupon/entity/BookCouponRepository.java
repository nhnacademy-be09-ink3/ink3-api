package shop.ink3.api.coupon.bookCoupon.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.book.book.entity.Book;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
    List<BookCoupon> findByBookId(Long bookId);
    List<BookCoupon> findByCouponId(Long couponId);
    Book book(Book book);
}
