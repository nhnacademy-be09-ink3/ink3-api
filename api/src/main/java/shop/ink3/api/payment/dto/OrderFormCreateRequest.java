package shop.ink3.api.payment.dto;

import shop.ink3.api.order.order.dto.OrderCreateRequest;
import shop.ink3.api.order.orderBook.dto.OrderBookListCreateRequest;
import shop.ink3.api.order.shipment.dto.ShipmentCreateRequest;

public record OrderFormCreateRequest(
        OrderCreateRequest orderCreateRequest,
        ShipmentCreateRequest shipmentCreateRequest,
        OrderBookListCreateRequest orderBookListCreateRequest
) {

}
