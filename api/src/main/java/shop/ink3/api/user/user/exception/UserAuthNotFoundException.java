package shop.ink3.api.user.user.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class UserAuthNotFoundException extends NotFoundException {
    public UserAuthNotFoundException(String loginId) {
        super("User for authentication not found. Login ID: %s".formatted(loginId));
    }
}
