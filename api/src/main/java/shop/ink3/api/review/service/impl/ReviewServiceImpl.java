package shop.ink3.api.review.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.review.repository.ReviewRepository;
import shop.ink3.api.review.service.ReviewService;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        Review review = ReviewRequest.toEntity(request);
        return ReviewResponse.from(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        List<ReviewResponse> responses = new ArrayList<>();

        for (Review review : reviews) {
            responses.add(ReviewResponse.from(review));
        }

        return responses;
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
