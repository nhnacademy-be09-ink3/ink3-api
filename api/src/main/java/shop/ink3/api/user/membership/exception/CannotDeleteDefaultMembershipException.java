package shop.ink3.api.user.membership.exception;

import shop.ink3.api.common.exception.BadRequestException;

public class CannotDeleteDefaultMembershipException extends BadRequestException {
    public CannotDeleteDefaultMembershipException() {
        super("Default membership cannot be deleted.");
    }
}
