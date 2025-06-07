package shop.ink3.api.payment.paymentUtil.parser.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.ink3.api.order.guest.dto.GuestPaymentConfirmRequest;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.dto.TossPaymentResponse;
import shop.ink3.api.payment.entity.Payment;
import shop.ink3.api.payment.entity.PaymentType;
import shop.ink3.api.payment.exception.PaymentParserFailException;
import shop.ink3.api.payment.paymentUtil.parser.PaymentParser;

@RequiredArgsConstructor
@Component("TOSS-PARSER")
public class TossPaymentParser implements PaymentParser {
    private final OrderRepository orderRepository;
    private static final String PAYMENT_METHOD = "TOSS";

    @Override
    public Payment paymentResponseParser(PaymentConfirmRequest paymentConfirmRequest, String json) {
        try{
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            TossPaymentResponse tossResponse = objectMapper.readValue(json, TossPaymentResponse.class);
            Order order = orderRepository.findById(paymentConfirmRequest.orderId())
                    .orElseThrow(()->new OrderNotFoundException(paymentConfirmRequest.orderId()));

            return Payment.builder()
                    .order(order)
                    .paymentKey(tossResponse.paymentKey())
                    .usedPoint(paymentConfirmRequest.usedPointAmount())
                    .discountPrice(paymentConfirmRequest.discountAmount())
                    .paymentAmount(tossResponse.totalAmount())
                    .paymentType(PaymentType.TOSS)
                    .requestAt(tossResponse.requestedAt().toLocalDateTime())
                    .approvedAt(tossResponse.approvedAt().toLocalDateTime())
                    .build();
        }  catch (Exception e) {
            throw new PaymentParserFailException(PAYMENT_METHOD, e);
        }
    }

    @Override
    public Payment paymentResponseParser(GuestPaymentConfirmRequest paymentConfirmRequest, String json) {
        try{
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            TossPaymentResponse tossResponse = objectMapper.readValue(json, TossPaymentResponse.class);
            Order order = orderRepository.findById(paymentConfirmRequest.orderId())
                    .orElseThrow(()->new OrderNotFoundException(paymentConfirmRequest.orderId()));

            return Payment.builder()
                    .order(order)
                    .paymentKey(tossResponse.paymentKey())
                    .usedPoint(0)
                    .discountPrice(0)
                    .paymentAmount(tossResponse.totalAmount())
                    .paymentType(PaymentType.TOSS)
                    .requestAt(tossResponse.requestedAt().toLocalDateTime())
                    .approvedAt(tossResponse.approvedAt().toLocalDateTime())
                    .build();
        }  catch (Exception e) {
            throw new PaymentParserFailException(PAYMENT_METHOD, e);
        }
    }
}
