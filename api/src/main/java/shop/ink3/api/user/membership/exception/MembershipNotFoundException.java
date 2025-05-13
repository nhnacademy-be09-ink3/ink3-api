package shop.ink3.api.user.membership.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class MembershipNotFoundException extends NotFoundException {
    public MembershipNotFoundException(long membershipId) {
        super("Membership not found. ID: %d".formatted(membershipId));
    }
}
