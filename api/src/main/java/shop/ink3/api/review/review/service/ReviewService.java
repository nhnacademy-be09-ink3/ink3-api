package shop.ink3.api.review.review.service;

import java.util.List;

import shop.ink3.api.review.review.dto.ReviewRequest;
import shop.ink3.api.review.review.dto.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByUserId(Long userId);

    void deleteReview(Long id);
}
