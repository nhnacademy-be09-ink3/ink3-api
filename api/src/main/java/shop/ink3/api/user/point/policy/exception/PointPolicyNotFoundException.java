package shop.ink3.api.user.point.policy.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class PointPolicyNotFoundException extends NotFoundException {
    public PointPolicyNotFoundException(long pointPolicyId) {
        super("Point policy not found. ID: " + pointPolicyId);
    }
}
