package shop.ink3.api.review.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.orderBook.exception.OrderBookNotFoundException;
import shop.ink3.api.order.orderBook.repository.OrderBookRepository;
import shop.ink3.api.review.review.dto.ReviewListResponse;
import shop.ink3.api.review.review.dto.ReviewRequest;
import shop.ink3.api.review.review.dto.ReviewResponse;
import shop.ink3.api.review.review.dto.ReviewUpdateRequest;
import shop.ink3.api.review.review.entity.Review;
import shop.ink3.api.review.review.exception.ReviewNotFoundException;
import shop.ink3.api.review.review.repository.ReviewRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final UserRepository userRepository;
    private final OrderBookRepository orderBookRepository;
    private final ReviewRepository reviewRepository;

    /**
     * Creates a new review for a specific user and order book.
     *
     * Retrieves the user and order book by their IDs from the request, throws an exception if either is not found, then creates and saves a new review with the provided details.
     *
     * @param request the review creation request containing user ID, order book ID, title, content, and rating
     * @return a response DTO representing the saved review
     * @throws UserNotFoundException if the user does not exist
     * @throws OrderBookNotFoundException if the order book does not exist
     */
    public ReviewResponse addReview(ReviewRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        OrderBook orderBook = orderBookRepository.findById(request.orderBookId())
            .orElseThrow(() -> new OrderBookNotFoundException(request.orderBookId()));

        Review review = Review.builder()
            .user(user)
            .orderBook(orderBook)
            .title(request.title())
            .content(request.content())
            .rating(request.rating())
            .build();
        return ReviewResponse.from(reviewRepository.save(review));
    }

    /****
     * Retrieves the review associated with the specified user ID.
     *
     * @param userId the ID of the user whose review is to be retrieved
     * @return a ReviewResponse representing the user's review
     */
    public ReviewResponse getReviewByUserId(Long userId) {
        Review review = reviewRepository.findReviewByUserId(userId);
        return ReviewResponse.from(review);
    }

    /**
     * Retrieves a paginated list of reviews for a specific book.
     *
     * @param pageable pagination and sorting information
     * @param bookId the ID of the book for which to retrieve reviews
     * @return a paginated response containing review summaries for the specified book
     */
    public PageResponse<ReviewListResponse> getReviewsByBookId(Pageable pageable, Long bookId) {
        Page<ReviewListResponse> page = reviewRepository
            .findListByBookId(pageable, bookId);

        return PageResponse.from(page);
    }

    /**
     * Updates the title, content, and rating of an existing review.
     *
     * @param reviewId the ID of the review to update
     * @param request the update request containing new title, content, and rating
     * @return a response DTO representing the updated review
     * @throws ReviewNotFoundException if the review with the given ID does not exist
     */
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        review.update(request.title(), request.content(), request.rating());

        return ReviewResponse.from(reviewRepository.save(review));
    }

    /****
     * Deletes a review by its ID.
     *
     * @param id the ID of the review to delete
     * @throws ReviewNotFoundException if no review with the given ID exists
     */
    public void deleteReview(Long id) {
        reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        reviewRepository.deleteById(id);
    }
}
