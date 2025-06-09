package shop.ink3.api.order.order.dto;

import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.orderBook.dto.OrderBookResponse;
import shop.ink3.api.order.shipment.dto.ShipmentResponse;
import shop.ink3.api.payment.dto.PaymentResponse;

public interface OrderDetailsResponse {
    OrderResponse getOrder();
    PageResponse<OrderBookResponse> getOrderBook();
    ShipmentResponse getShipment();
    PaymentResponse getPayment();
}
