package shop.ink3.api.review.review.exception;

public class ReviewAlreadyRegisterException extends RuntimeException {
    public ReviewAlreadyRegisterException(Long orderBookId) {
        super("ID " + orderBookId + "는 이미 리뷰가 작성된 도서입니다.");
    }
}

