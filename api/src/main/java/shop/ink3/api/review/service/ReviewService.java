package shop.ink3.api.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.review.exception.ReviewNotFoundException;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.entity.Review;
import shop.ink3.api.review.repository.ReviewRepository;
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

    public Page<ReviewResponse> getReviewsByBookId(Pageable pageable, Long bookId) {
        return reviewRepository.findAllByOrderBook_BookId(pageable, bookId)
            .map(ReviewResponse::from);
    }

    public void deleteReview(Long id) {
        reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        reviewRepository.deleteById(id);
    }
}
