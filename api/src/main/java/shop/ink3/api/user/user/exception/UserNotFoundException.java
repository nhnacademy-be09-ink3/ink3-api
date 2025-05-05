package shop.ink3.api.user.user.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(long userId) {
        super("User not found. ID: %d".formatted(userId));
    }
}
