package shop.ink3.api.payment.dto;

import shop.ink3.api.payment.entity.PaymentType;

public record PaymentConfirmRequest (
        Long userId,
        Long orderId,
        String paymentKey,
        String orderUUID,
        Integer discountAmount,
        Integer usedPointAmount,
        Integer amount,
        PaymentType paymentType
){
}
