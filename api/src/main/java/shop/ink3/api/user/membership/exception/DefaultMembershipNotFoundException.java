package shop.ink3.api.user.membership.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class DefaultMembershipNotFoundException extends NotFoundException {
    public DefaultMembershipNotFoundException() {
        super("Default membership not found.");
    }
}
