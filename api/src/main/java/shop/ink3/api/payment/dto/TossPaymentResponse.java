package shop.ink3.api.payment.dto;

import java.time.LocalDateTime;

public record TossPaymentResponse (
        String paymentKey,
        int totalAmount,
        OffsetDateTime requestedAt,
        OffsetDateTime  approvedAt
){
}
