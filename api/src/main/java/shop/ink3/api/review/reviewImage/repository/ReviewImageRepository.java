package shop.ink3.api.review.reviewImage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.ink3.api.review.reviewImage.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReviewId(Long reviewId);

    List<ReviewImage> findByReviewIdIn(List<Long> reviewIds);
}
