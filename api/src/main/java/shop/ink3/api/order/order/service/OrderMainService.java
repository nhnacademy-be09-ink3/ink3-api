package shop.ink3.api.order.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.orderBook.service.OrderBookService;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.dto.RefundResponse;
import shop.ink3.api.order.refund.service.RefundService;
import shop.ink3.api.order.shipment.service.ShipmentService;
import shop.ink3.api.order.order.dto.OrderFormCreateRequest;
import shop.ink3.api.user.point.eventListener.PointHistoryAfterCancelPaymentEven;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderMainService {

    private final OrderService orderService;
    private final OrderBookService orderBookService;
    private final ShipmentService shipmentService;
    private final RefundService refundService;
    private final ApplicationEventPublisher applicationEventPublisher;

    // 결제 시 주문서 생성 (주문 관련 데이터 저장)
    public OrderResponse createOrderForm(OrderFormCreateRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request.orderCreateRequest());
        orderBookService.createOrderBook(orderResponse.getId(), request.createRequestList());
        shipmentService.createShipment(orderResponse.getId(), request.shipmentCreateRequest());
        return orderResponse;
    }

    // 반품 생성
    public RefundResponse createRefund(RefundCreateRequest request) {
        // 반품 가능 여부 확인
        refundService.availableRefund(request);
        RefundResponse refund = refundService.createRefund(request);
        //TODO : 논의 사항 = 포인트를 이벤트 리스너로 분리   OR    MQ로 분리하여 처리
        //TODO 금액 환불 및 포인트 내역 추가
/*        OrderResponse order = orderService.getOrder(refund.getOrderId());
        applicationEventPublisher.publishEvent(
                new PointHistoryAfterCancelPaymentEven(order.getId(), order.getPointHistoryId())
        );*/
        //TODO : 사용된 쿠폰 재발급

        orderService.updateOrderStatus(request.getOrderId(), new OrderStatusUpdateRequest(OrderStatus.REFUNDED));
        return refund;
    }


    //TODO : 결제 실패 시
}
