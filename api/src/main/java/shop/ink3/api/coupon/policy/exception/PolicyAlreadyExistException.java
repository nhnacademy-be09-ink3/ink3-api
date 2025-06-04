package shop.ink3.api.coupon.policy.exception;

public class PolicyAlreadyExistException extends RuntimeException {
    public PolicyAlreadyExistException(String message) {
        super(message);
    }
}
