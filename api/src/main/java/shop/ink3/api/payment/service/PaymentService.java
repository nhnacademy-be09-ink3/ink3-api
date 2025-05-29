package shop.ink3.api.payment.service;

import java.util.Objects;
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
import shop.ink3.api.user.point.eventListener.PointHistoryAfterCancelPaymentEven;
import shop.ink3.api.user.point.eventListener.PointHistoryAfterPaymentEven;

@Slf4j
@Transactional
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

    // 결제 승인 API 호출 및 Payment 객체 반환
    // 트랜잭션 안거쳐야함 외부 API이기 때문.
    @Transactional(readOnly = true)
    public Payment callPaymentAPI(PaymentConfirmRequest confirmRequest){
        // 결제 승인 요청
        String paymentType = String.valueOf(confirmRequest.paymentType()).toUpperCase();
        PaymentProcessor paymentProcessor = paymentProcessorResolver.getPaymentProcessor(
                String.format("%s-%s", paymentType,PROCESSOR));
        String paymentApproveResponse = paymentProcessor.processPayment(confirmRequest);
        // 결제 응답 파서
        PaymentParser paymentParser = paymentResponseParserResolver.getPaymentParser(
                String.format("%s-%s", paymentType, PARSER));
        Payment payment = paymentParser.paymentResponseParser(confirmRequest, paymentApproveResponse);

        payment.setDiscountPrice(confirmRequest.discountAmount());
        payment.setUsedPoint(confirmRequest.usedPointAmount());
        return payment;
    }


    // 결제 취소
    public void cancelPayment(long orderId, long userId){
        OrderResponse orderResponse = orderService.getOrder(orderId);
        // 결제 취소 가능 여부 확인
        if(!orderResponse.getStatus().equals(OrderStatus.CONFIRMED)){
            throw new PaymentCancelNotAllowedException();
        }

        //TODO 논의 사항 = 포인트를 이벤트 리스너로 분리   OR    MQ로 분리하여 처리
        //TODO 금액 환불 및 포인트 내역 추가
/*        eventPublisher.publishEvent(
                new PointHistoryAfterCancelPaymentEven(orderResponse.getId(), orderResponse.getPointHistoryId())
        );*/
        //TODO : 사용된 쿠폰 재발급

        // 주문된 도서들의 재고를 원상복구
        orderBookService.resetBookQuantity(orderId);
        // 주문 상태 변경
        orderService.updateOrderStatus(orderId, new OrderStatusUpdateRequest(OrderStatus.CANCELLED));
    }

    // 생성
    public PaymentResponse createPayment(long userId, Payment payment){
        // 특정 주문에 대한 payment가 존재하는지 확인 정도.
        Long orderId = payment.getOrder().getId();
        Optional<Payment> optionalPayment = paymentRepository.findByOrderId(orderId);
        if(Objects.nonNull(optionalPayment)){
            throw new PaymentAlreadyExistsException(orderId);
        }
        Payment savePayment = paymentRepository.save(payment);

        //TODO 논의 사항 = 포인트를 이벤트 리스너로 분리   OR    MQ로 분리하여 처리
        // 포인트 사용 및 적립
        eventPublisher.publishEvent(
                new PointHistoryAfterPaymentEven(
                        userId,
                        payment.getOrder().getId(),
                        payment.getPaymentAmount(),
                        payment.getUsedPoint()));
        return PaymentResponse.from(savePayment);
    }

    // 조회
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(long orderId){
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        return PaymentResponse.from(payment);
    }

    // 삭제
    public void deletePayment(long orderId){
        orderRepository.findById(orderId)
                .orElseThrow(()->new OrderNotFoundException(orderId));
        paymentRepository.deleteByOrderId(orderId);
    }
}
