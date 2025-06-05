package shop.ink3.api.user.membership.exception;

import shop.ink3.api.common.exception.BadRequestException;

public class CannotDeactivateDefaultMembershipException extends BadRequestException {
    public CannotDeactivateDefaultMembershipException() {
        super("Default membership cannot be deactivated.");
    }
}
