package shop.ink3.api.order.refund.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.refund.entity.Refund;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefundResponse {
    private Long id;
    private Long orderId;
    private String reason;
    private String details;
    private Integer RefundShippingFee;
    private LocalDateTime createdAt;

    public static RefundResponse from(Refund refund) {
        return new RefundResponse(
                refund.getId(),
                refund.getOrder().getId(),
                refund.getReason(),
                refund.getDetails(),
                refund.getRefundShippingFee(),
                refund.getCreatedAt()
        );
    }
}
