package shop.ink3.api.user.point.policy.exception;

import shop.ink3.api.common.exception.BadRequestException;

public class CannotDeleteActivePointPolicyException extends BadRequestException {
    public CannotDeleteActivePointPolicyException() {
        super("Cannot delete an active membership.");
    }
}
