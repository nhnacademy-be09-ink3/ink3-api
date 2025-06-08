package shop.ink3.api.payment.dto;

public record ZeroPaymentRequest(
    Long userId,
    Long orderId,
    Integer discountAmount,
    Integer usedPointAmount,
    Integer amount
){
}
