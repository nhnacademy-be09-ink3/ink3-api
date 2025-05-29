package shop.ink3.api.user.like.exception;

import shop.ink3.api.common.exception.AlreadyExistsException;

public class LikeAlreadyExistsException extends AlreadyExistsException {
    public LikeAlreadyExistsException(long userId, long bookId) {
        super("Like already exists. User ID: %d, Book ID: %d".formatted(userId, bookId));
    }
}
