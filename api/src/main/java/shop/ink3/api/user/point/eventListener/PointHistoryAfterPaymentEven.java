package shop.ink3.api.user.point.eventListener;

public record PointHistoryAfterPaymentEven(long userId ,long orderId, int paymentAmount, int usedPointAmount) {
}
