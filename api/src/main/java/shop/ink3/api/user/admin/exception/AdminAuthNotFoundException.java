package shop.ink3.api.user.admin.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class AdminAuthNotFoundException extends NotFoundException {
    public AdminAuthNotFoundException(String loginId) {
        super("Admin for authentication not found. Login ID: %s".formatted(loginId));
    }
}
