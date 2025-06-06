package shop.ink3.api.user.point.history.eventListener;

public record PointHistoryAfterPaymentEven(long userId, long orderId, int paymentAmount, int usedPointAmount) {
}
