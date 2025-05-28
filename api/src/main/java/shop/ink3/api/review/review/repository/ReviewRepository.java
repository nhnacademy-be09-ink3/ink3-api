package shop.ink3.api.review.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.ink3.api.review.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findReviewByUserId(Long userId);

    @Query("SELECT r FROM Review r WHERE r.orderBook.book.id = :bookId")
    Page<Review> findAllByBookId(Pageable pageable, @Param("bookId") Long bookId);
}
