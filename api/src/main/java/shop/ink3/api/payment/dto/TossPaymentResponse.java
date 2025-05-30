package shop.ink3.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentResponse (
        String paymentKey,
        int totalAmount,
        OffsetDateTime requestedAt,
        OffsetDateTime  approvedAt
){
}
