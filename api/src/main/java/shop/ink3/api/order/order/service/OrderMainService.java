package shop.ink3.api.order.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import shop.ink3.api.user.point.eventListener.PointEventListener;
import shop.ink3.api.user.point.eventListener.PointHistoryAfterCancelPaymentEven;

@RequiredArgsConstructor
@Service
public class OrderMainService {

    private final OrderService orderService;
    private final OrderBookService orderBookService;
    private final ShipmentService shipmentService;
    private final RefundService refundService;
    private final PointEventListener pointEventListener;

    // 결제 시 주문서 생성 (주문 관련 데이터 저장)
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderResponse createOrderForm(OrderFormCreateRequest request){
        OrderResponse orderResponse = orderService.createOrder(request.orderCreateRequest());
        orderBookService.createOrderBook(request.createRequestList());
        shipmentService.createShipment(request.shipmentCreateRequest());
        return orderResponse;
    }
    //TODO 비회원일 경우 저장해줘야함



    // 반품 생성
    @Transactional(propagation = Propagation.REQUIRED)
    public RefundResponse createRefund(RefundCreateRequest request) {
        // 반품 가능 여부 확인하기
        refundService.availableRefund(request);
        // 반품 신청
        RefundResponse refund = refundService.createRefund(request);
        //TODO : 금액 환불 -> 포인트 내역 추가 / 포인트를 리스너로 분리하여 사용할지, MQ로 분리하여 처리할지.
        OrderResponse order = orderService.getOrder(refund.getOrderId());
        pointEventListener.handlePointHistoryAfterCancelPayment(
                new PointHistoryAfterCancelPaymentEven(order.getId(), order.getPointHistoryId())
        );

        //TODO : 사용된 쿠폰 재발급


        // 주문 상태 반품으로 변경
        orderService.updateOrderStatus(request.getOrderId(), new OrderStatusUpdateRequest(OrderStatus.REFUNDED));
        return refund;
    }
    //TODO 비회원일 경우도 되돌려줘야함
}
