package shop.ink3.api.order.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.orderBook.service.OrderBookService;
import shop.ink3.api.order.orderPoint.service.OrderPointService;
import shop.ink3.api.order.orderPoint.entity.OrderPoint;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.dto.RefundResponse;
import shop.ink3.api.order.refund.service.RefundService;
import shop.ink3.api.order.shipment.service.ShipmentService;
import shop.ink3.api.order.order.dto.OrderFormCreateRequest;
import shop.ink3.api.payment.dto.PaymentResponse;
import shop.ink3.api.payment.service.PaymentService;
import shop.ink3.api.user.point.service.PointService;
import shop.ink3.api.user.user.dto.UserPointRequest;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderMainService {

    private final OrderService orderService;
    private final OrderBookService orderBookService;
    private final ShipmentService shipmentService;
    private final RefundService refundService;
    private final PaymentService paymentService;
    private final PointService pointService;
    private final OrderPointService orderPointService;

    // 결제 시 주문서 생성 (주문 관련 데이터 저장)
    public OrderResponse createOrderForm(OrderFormCreateRequest request) {
        OrderResponse orderResponse = orderService.createOrder(request.orderCreateRequest());
        orderBookService.createOrderBook(orderResponse.getId(), request.createRequestList());
        shipmentService.createShipment(orderResponse.getId(), request.shipmentCreateRequest());
        return orderResponse;
    }

    // 반품 생성 (신청)
    public RefundResponse createRefund(RefundCreateRequest request) {
        // 반품 가능 여부 확인
        refundService.availableRefund(request);

        RefundResponse refund = refundService.createRefund(request);
        orderService.updateOrderStatus(request.getOrderId(), new OrderStatusUpdateRequest(OrderStatus.REFUNDED));
        return refund;
    }

    //TODO : 사용된 쿠폰 재발급
    // 반품 승인
    public void approveRefund(long userId, long orderId){
        RefundResponse refund = refundService.getOrderRefund(orderId);
        PaymentResponse payment = paymentService.getPayment(refund.getId());

        // 결제 금액 환불
        String description = String.format("반품처리로 인한 환불금액 (반품비 (%d원) 제외)", refund.getRefundShippingFee());
        pointService.earnPoint(userId, new UserPointRequest(payment.paymentAmount() - refund.getRefundShippingFee(), description));

        // 포인트 취소 (사용한 것도 취소 적립된 것도 취소)
        List<OrderPoint> orderPoints = orderPointService.getOrderPoints(refund.getId());
        for(OrderPoint orderPoint : orderPoints) {
            pointService.cancelPoint(refund.getId(), orderPoint.getPointHistory().getId());
        }
    }
}
