package shop.ink3.api.order.refund.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.refund.entity.Refund;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefundResponse {
    private Long id;
    private Order order;
    private String reason;
    private String details;

    public static RefundResponse from(Refund refund) {
        return new RefundResponse(
                refund.getId(),
                refund.getOrder(),
                refund.getReason(),
                refund.getDetails()
        );
    }
}
