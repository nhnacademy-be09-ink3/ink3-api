package shop.ink3.api.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import shop.ink3.api.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByUserId(Long userId);

    Page<Review> findAllByOrderBook_BookId(Pageable pageable, Long bookId);
}
