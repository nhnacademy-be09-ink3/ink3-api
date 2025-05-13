package shop.ink3.api.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.review.common.exception.ReviewNotFoundException;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService  {
    private final ReviewRepository reviewRepository;

    public ReviewResponse addReview(ReviewRequest request) {
        Review review = ReviewRequest.toEntity(request);
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public ReviewResponse getReviewByUserId(Long userId) {
        Review review = reviewRepository.findByUserId(userId);
        return ReviewResponse.from(review);
    }

    public Page<ReviewResponse> getReviewsByBookId(Pageable pageable, Long userId) {
        return reviewRepository.findAllByOrderBook_BookId(pageable, userId)
            .map(ReviewResponse::from);
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException("존재하지 않는 리뷰입니다."));
        reviewRepository.deleteById(id);
    }
}
