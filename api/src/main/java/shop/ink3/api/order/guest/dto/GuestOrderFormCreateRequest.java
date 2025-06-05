package shop.ink3.api.order.guest.dto;
import java.util.List;
import shop.ink3.api.order.orderBook.dto.OrderBookCreateRequest;
import shop.ink3.api.order.shipment.dto.ShipmentCreateRequest;

public record GuestOrderFormCreateRequest(
    GuestCreateRequest guestCreateRequest,
    GuestOrderCreateRequest guestOrderCreateRequest,
    ShipmentCreateRequest shipmentCreateRequest,
    List<OrderBookCreateRequest> createRequestList,
    int paymentAmount
){
}
