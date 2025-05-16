package shop.ink3.api.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.order.service.OrderService;
import shop.ink3.api.order.orderBook.service.OrderBookService;
import shop.ink3.api.order.shipment.service.ShipmentService;
import shop.ink3.api.payment.dto.OrderFormCreateRequest;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.dto.PaymentResponse;
import shop.ink3.api.payment.entity.Payment;
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

    private final String PARSER = "PARSER";
    private final String PROCESSOR = "PROCESSOR";



    // 결제 시 주문서 생성 (주문 관련 데이터 저장)
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrderForm(OrderFormCreateRequest request){
        OrderResponse order = orderService.createOrder(request.orderCreateRequest());
        orderBookService.createOrderBook(order.getId(),request.orderBookListCreateRequest());
        shipmentService.createShipment(request.shipmentCreateRequest());
    }

    // db 저장
    @Transactional
    public PaymentResponse createPayment(Payment payment){
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


    // 결제 승인 API 호출 및 Payment 객체 반환
    public Payment callPaymentAPI(PaymentConfirmRequest confirmRequest){
        // 결제 승인 요청
        PaymentProcessor paymentProcessor = paymentProcessorResolver.getPaymentProcessor(
                String.format("%s-%s",String.valueOf(confirmRequest.paymentType()).toUpperCase(),PROCESSOR));
        String paymentApproveResponse = paymentProcessor.processPayment(confirmRequest);

        // 결제 응답 파서
        PaymentParser paymentParser = paymentResponseParserResolver.getPaymentParser(
                String.format("%s-%s",String.valueOf(confirmRequest.paymentType()).toUpperCase(),PARSER));
        Payment payment = paymentParser.paymentResponseParser(confirmRequest.orderId(), paymentApproveResponse);

        return payment;
    }
}
