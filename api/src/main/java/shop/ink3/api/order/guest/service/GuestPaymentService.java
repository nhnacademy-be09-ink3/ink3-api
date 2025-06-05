package shop.ink3.api.order.guest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.guest.dto.GuestPaymentConfirmRequest;
import shop.ink3.api.order.order.dto.OrderStatusUpdateRequest;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.orderBook.service.OrderBookService;
import shop.ink3.api.payment.dto.PaymentResponse;
import shop.ink3.api.payment.entity.Payment;
import shop.ink3.api.payment.exception.PaymentAlreadyExistsException;
import shop.ink3.api.payment.paymentUtil.parser.PaymentParser;
import shop.ink3.api.payment.paymentUtil.processor.PaymentProcessor;
import shop.ink3.api.payment.paymentUtil.resolver.PaymentProcessorResolver;
import shop.ink3.api.payment.paymentUtil.resolver.PaymentResponseParserResolver;
import shop.ink3.api.payment.repository.PaymentRepository;
import shop.ink3.api.payment.service.PaymentService;

@Transactional
@RequiredArgsConstructor
@Service
public class GuestPaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final GuestOrderService guestOrderService;
    private final OrderBookService orderBookService;
    private final PaymentProcessorResolver paymentProcessorResolver;
    private final PaymentResponseParserResolver paymentResponseParserResolver;


    // 결제 승인 API 호출 및 ApproveResponse 반환
    @Transactional(readOnly = true)
    public String callPaymentAPI(GuestPaymentConfirmRequest confirmRequest) {
        PaymentProcessor paymentProcessor = paymentProcessorResolver.getPaymentProcessor(
                String.format("%s-%s", String.valueOf(confirmRequest.paymentType()).toUpperCase(), "PROCESSOR"));
        return paymentProcessor.processPayment(confirmRequest);
    }


    // 생성 (결제 성공)
    public PaymentResponse createPayment(GuestPaymentConfirmRequest confirmRequest, String paymentApproveResponse) {
        // 특정 주문에 대한 payment가 존재하는지 확인.
        if (paymentRepository.findByOrderId(confirmRequest.orderId()).isPresent()) {
            throw new PaymentAlreadyExistsException(confirmRequest.orderId());
        }

        PaymentParser paymentParser = paymentResponseParserResolver.getPaymentParser(
                String.format("%s-%s", String.valueOf(confirmRequest.paymentType()).toUpperCase(), "PARSER"));
        Payment payment = paymentParser.paymentResponseParser(confirmRequest, paymentApproveResponse);

        guestOrderService.updateOrderStatus(confirmRequest.orderId(), new OrderStatusUpdateRequest(OrderStatus.CONFIRMED));
        return PaymentResponse.from(paymentRepository.save(payment));
    }

    // 결제 실패 (비회원)
    public void failPayment(long orderId) {
        orderBookService.resetBookQuantity(orderId);
        orderService.updateOrderStatus(orderId, new OrderStatusUpdateRequest(OrderStatus.FAILED));
    }

}
