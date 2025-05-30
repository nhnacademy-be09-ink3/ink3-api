package shop.ink3.api.review.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(long reviewId) {
        super("존재하지 않는 리뷰입니다. id: %d".formatted(reviewId));
    }
}
