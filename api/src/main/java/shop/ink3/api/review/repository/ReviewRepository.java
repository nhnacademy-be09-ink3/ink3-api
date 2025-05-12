package shop.ink3.api.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.ink3.api.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
}
