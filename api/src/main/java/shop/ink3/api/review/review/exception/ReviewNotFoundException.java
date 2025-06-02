package shop.ink3.api.review.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    /****
     * Constructs a ReviewNotFoundException for a review that does not exist.
     *
     * @param reviewId the ID of the review that was not found
     */
    public ReviewNotFoundException(long reviewId) {
        super("존재하지 않는 리뷰입니다. id: %d".formatted(reviewId));
    }
}
