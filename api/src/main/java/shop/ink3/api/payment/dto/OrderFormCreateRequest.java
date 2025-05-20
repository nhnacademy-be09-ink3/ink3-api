package shop.ink3.api.payment.dto;

import java.util.List;
import shop.ink3.api.order.order.dto.OrderCreateRequest;
import shop.ink3.api.order.orderBook.dto.OrderBookCreateRequest;
import shop.ink3.api.order.shipment.dto.ShipmentCreateRequest;

public record OrderFormCreateRequest(
        OrderCreateRequest orderCreateRequest,
        ShipmentCreateRequest shipmentCreateRequest,
        List<OrderBookCreateRequest> createRequestList
) {

}
