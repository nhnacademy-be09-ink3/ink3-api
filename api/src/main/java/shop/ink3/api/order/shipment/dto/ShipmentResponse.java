package shop.ink3.api.order.shipment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.shipment.entity.Shipment;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ShipmentResponse {
    private Long id;
    private Long orderId;
    private LocalDate preferredDeliveryDate;
    private LocalDateTime deliveredAt;
    private String recipientName;
    private String recipientPhone;
    private Integer postalCode;
    private String defaultAddress;
    private String detailAddress;
    private String extraAddress;
    private Integer shippingFee;
    private String shippingCode;

    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getOrder().getId(),
                shipment.getPreferredDeliveryDate(),
                shipment.getDeliveredAt(),
                shipment.getRecipientName(),
                shipment.getRecipientPhone(),
                shipment.getPostalCode(),
                shipment.getDefaultAddress(),
                shipment.getDetailAddress(),
                shipment.getExtraAddress(),
                shipment.getShippingFee(),
                shipment.getShippingCode()
        );
    }
}
