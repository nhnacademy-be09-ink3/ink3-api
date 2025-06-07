package shop.ink3.api.order.guest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.guest.dto.GuestOrderFormCreateRequest;
import shop.ink3.api.order.guest.dto.GuestOrderResponse;
import shop.ink3.api.order.orderBook.service.OrderBookService;
import shop.ink3.api.order.shipment.service.ShipmentService;

@Transactional
@RequiredArgsConstructor
@Service
public class GuestOrderMainService {
    private final GuestOrderService guestOrderService;
    private final GuestService guestService;
    private final OrderBookService orderBookService;
    private final ShipmentService shipmentService;

    // 결제 시 비회원 주문 생성
    public GuestOrderResponse createGuestOrderForm(GuestOrderFormCreateRequest request){
        GuestOrderResponse guestOrderResponse = guestOrderService.createGuestOrder(request.guestOrderCreateRequest());
        guestService.createGuest(guestOrderResponse.orderId(), request.guestCreateRequest());
        orderBookService.createOrderBook(guestOrderResponse.orderId(), request.createRequestList());
        shipmentService.createShipment(guestOrderResponse.orderId(), request.shipmentCreateRequest());
        return guestOrderResponse;
    }
}
