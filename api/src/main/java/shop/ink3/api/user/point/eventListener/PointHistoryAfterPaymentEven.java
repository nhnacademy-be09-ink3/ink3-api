package shop.ink3.api.user.point.eventListener;

public record PointHistoryAfterPaymentEven(long orderId, int amount) {
}
