package shop.ink3.api.payment.service;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.exception.AlreadyExistsException;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.orderBook.service.OrderBookService;
import shop.ink3.api.order.refund.dto.RefundCreateRequest;
import shop.ink3.api.order.refund.service.RefundService;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyResponse;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;
import shop.ink3.api.order.refundPolicy.service.RefundPolicyService;
import shop.ink3.api.order.shipment.service.ShipmentService;
import shop.ink3.api.payment.dto.OrderFormCreateRequest;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.dto.PaymentResponse;
import shop.ink3.api.payment.entity.Payment;
import shop.ink3.api.payment.exception.PaymentAlreadyExistsException;
import shop.ink3.api.payment.exception.PaymentCancelNotAllowedException;
import shop.ink3.api.payment.exception.PaymentNotFoundException;
import shop.ink3.api.payment.paymentUtil.parser.PaymentParser;
import shop.ink3.api.payment.paymentUtil.processor.PaymentProcessor;
import shop.ink3.api.payment.paymentUtil.resolver.PaymentProcessorResolver;
import shop.ink3.api.payment.paymentUtil.resolver.PaymentResponseParserResolver;
import shop.ink3.api.payment.repository.PaymentRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderBookService orderBookService;
    private final ShipmentService shipmentService;
    private final PaymentProcessorResolver paymentProcessorResolver;
    private final PaymentResponseParserResolver paymentResponseParserResolver;

    private static final String PARSER = "PARSER";
    private static final String PROCESSOR = "PROCESSOR";

    // 결제 승인 API 호출 및 Payment 객체 반환
    public Payment callPaymentAPI(PaymentConfirmRequest confirmRequest){
        // 결제 승인 요청
        PaymentProcessor paymentProcessor = paymentProcessorResolver.getPaymentProcessor(
                String.format("%s-%s",String.valueOf(confirmRequest.paymentType()).toUpperCase(),PROCESSOR));
        String paymentApproveResponse = paymentProcessor.processPayment(confirmRequest);
        // 결제 응답 파서
        PaymentParser paymentParser = paymentResponseParserResolver.getPaymentParser(
                String.format("%s-%s", String.valueOf(confirmRequest.paymentType()).toUpperCase(), PARSER));
        Payment payment = paymentParser.paymentResponseParser(confirmRequest.orderId(), paymentApproveResponse);
        return payment;
    }

    // 결제 취소
    @Transactional(propagation = Propagation.REQUIRED)
    public void cancelPayment(long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        // 결제 취소 가능 여부 확인
        if(!order.getStatus().equals(OrderStatus.CONFIRMED)){
            throw new PaymentCancelNotAllowedException();
        }
        //TODO : 금액 환불 -> 포인트 내역 추가 (아직 로직 없음)

        //TODO : 사용된 쿠폰 재발급 (아직 로직 없음)

        // 주문된 도서들의 재고를 원상복구
        orderBookService.resetBookQuantity(orderId);
        // 주문 상태 변경
        orderService.updateOrderStatus(orderId, new OrderStatusUpdateRequest(OrderStatus.CANCELLED));
    }

    // 생성
    @Transactional
    public PaymentResponse createPayment(Payment payment){
        // 특정 주문에 대한 payment가 존재하는지 확인 정도.
        Long orderId = payment.getOrder().getId();
        Optional<Payment> optionalPayment = paymentRepository.findByOrder_Id(orderId);
        if(Objects.nonNull(optionalPayment)){
            throw new PaymentAlreadyExistsException(orderId);
        }
        Payment savePayment = paymentRepository.save(payment);
        return PaymentResponse.from(savePayment);
    }

    // 조회
    public PaymentResponse getPayment(long orderId){
        Payment payment = paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        return PaymentResponse.from(payment);
    }

    // 삭제
    @Transactional
    public void deletePayment(long orderId){
        orderRepository.findById(orderId)
                .orElseThrow(()->new OrderNotFoundException(orderId));
        paymentRepository.deleteByOrder_Id(orderId);
    }
}
