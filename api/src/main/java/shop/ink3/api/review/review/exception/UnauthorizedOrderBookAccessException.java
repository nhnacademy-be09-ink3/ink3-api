package shop.ink3.api.review.review.exception;

public class UnauthorizedOrderBookAccessException extends RuntimeException {
    public UnauthorizedOrderBookAccessException(Long userId, Long orderBookId) {
        super("User " + userId + " is not allowed to access orderBook " + orderBookId);
    }
}
