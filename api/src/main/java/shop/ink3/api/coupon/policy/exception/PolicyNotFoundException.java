package shop.ink3.api.coupon.policy.exception;

public class PolicyNotFoundException extends RuntimeException {
  public PolicyNotFoundException(String message) {
    super(message);
  }
}
