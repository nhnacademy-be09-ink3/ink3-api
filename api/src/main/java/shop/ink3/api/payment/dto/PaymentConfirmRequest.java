package shop.ink3.api.payment.dto;

import shop.ink3.api.payment.entity.PaymentType;

public record PaymentConfirmRequest (
    Long orderId,
    String paymentKey,
    String orderUUID,
    Integer amount,
    PaymentType paymentType
){

}
