package shop.ink3.api.payment.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.orderBook.service.OrderBookService;
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
    private final PaymentProcessorResolver paymentProcessorResolver;
    private final PaymentResponseParserResolver paymentResponseParserResolver;
    private final ApplicationEventPublisher eventPublisher;

    private static final String PARSER = "PARSER";
    private static final String PROCESSOR = "PROCESSOR";

    // 결제 승인 API 호출 및 ApproveResponse 반환
    public String callPaymentAPI(PaymentConfirmRequest confirmRequest) {
        PaymentProcessor paymentProcessor = paymentProcessorResolver.getPaymentProcessor(
                String.format("%s-%s", String.valueOf(confirmRequest.paymentType()).toUpperCase(), PROCESSOR));
        return paymentProcessor.processPayment(confirmRequest);
    }

    //TODO 포인트 내역 추가
    // 생성 (결제 성공)
    @Transactional
    public PaymentResponse createPayment(long userId, PaymentConfirmRequest confirmRequest, String paymentApproveResponse) {
        PaymentParser paymentParser = paymentResponseParserResolver.getPaymentParser(
                String.format("%s-%s", String.valueOf(confirmRequest.paymentType()).toUpperCase(), PARSER));
        Payment payment = paymentParser.paymentResponseParser(confirmRequest, paymentApproveResponse);
        payment.updateDiscountAndPoint(confirmRequest.usedPointAmount(), confirmRequest.discountAmount());

        // 특정 주문에 대한 payment가 존재하는지 확인.
        Optional<Payment> optionalPayment = paymentRepository.findByOrderId(confirmRequest.orderId());
        if (optionalPayment.isPresent()) {
            throw new PaymentAlreadyExistsException(confirmRequest.orderId());
        }

        return PaymentResponse.from(paymentRepository.save(payment));
    }

    //TODO 금액 환불 및 포인트 내역 추가
    //TODO : 사용된 쿠폰 재발급
    // 결제 실패
    @Transactional
    public void failPayment(long orderId, long userId) {
        // 주문된 도서들의 재고를 원상복구
        orderBookService.resetBookQuantity(orderId);
        // 주문 상태 변경
        orderService.updateOrderStatus(orderId, new OrderStatusUpdateRequest(OrderStatus.FAILED));
    }


    //TODO 금액 환불 및 포인트 내역 추가
    //TODO : 사용된 쿠폰 재발급
    // 결제 취소
    @Transactional
    public void cancelPayment(long orderId, long userId) {
        OrderResponse orderResponse = orderService.getOrder(orderId);
        // 결제 취소 가능 여부 확인
        if (!orderResponse.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new PaymentCancelNotAllowedException();
        }

        // 주문된 도서들의 재고를 원상복구
        orderBookService.resetBookQuantity(orderId);
        // 주문 상태 변경
        orderService.updateOrderStatus(orderId, new OrderStatusUpdateRequest(OrderStatus.CANCELLED));
    }

    // 조회
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        return PaymentResponse.from(payment);
    }

    // 삭제
    @Transactional
    public void deletePayment(long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        paymentRepository.deleteByOrderId(orderId);
    }
}
