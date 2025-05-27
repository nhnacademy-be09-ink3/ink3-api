package shop.ink3.api.review.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
import shop.ink3.api.review.review.exception.ReviewNotFoundException;
import shop.ink3.api.review.review.dto.ReviewRequest;
import shop.ink3.api.review.review.dto.ReviewResponse;
import shop.ink3.api.review.review.entity.Review;
import shop.ink3.api.review.review.repository.ReviewRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService  {
    private final UserRepository userRepository;
    private final OrderBookRepository orderBookRepository;
    private final ReviewRepository reviewRepository;

    public ReviewResponse addReview(ReviewRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        OrderBook orderBook = orderBookRepository.findById(request.orderBookId()).orElseThrow(() -> new OrderBookNotFoundException(request.orderBookId()));

        Review review = Review.builder()
            .user(user)
            .orderBook(orderBook)
            .title(request.title())
            .content(request.content())
            .rating(request.rating())
            .build();
        return ReviewResponse.from(reviewRepository.save(review));
    }

    public ReviewResponse getReviewByUserId(Long userId) {
        Review review = reviewRepository.findReviewByUserId(userId);
        return ReviewResponse.from(review);
    }

    public PageResponse<ReviewResponse> getReviewsByBookId(Pageable pageable, Long bookId) {
        Page<ReviewResponse> page = reviewRepository
            .findAllByBookId(pageable, bookId)
            .map(ReviewResponse::from);

        return PageResponse.from(page);
    }

    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        review.update(request.title(), request.content(), request.rating());

        return ReviewResponse.from(reviewRepository.save(review));
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        reviewRepository.deleteById(id);
    }
}
