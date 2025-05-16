package shop.ink3.api.payment.paymentUtil.parser.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.payment.dto.TossPaymentResponse;
import shop.ink3.api.payment.entity.Payment;
import shop.ink3.api.payment.entity.PaymentType;
import shop.ink3.api.payment.paymentUtil.parser.PaymentParser;

@RequiredArgsConstructor
@Component("TOSS-PARSER")
public class TossPaymentParser implements PaymentParser {
    private final OrderRepository orderRepository;

    @Override
    public Payment paymentResponseParser(long orderId, String json) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            TossPaymentResponse tossResponse = objectMapper.readValue(json, TossPaymentResponse.class);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(()->new OrderNotFoundException(orderId));

            return Payment.builder()
                    .order(order)
                    .paymentKey(tossResponse.paymentKey())
                    .paymentAmount(tossResponse.totalAmount())
                    .paymentType(PaymentType.TOSS)
                    .requestAt(tossResponse.requestedAt())
                    .approvedAt(tossResponse.approvedAt())
                    .build();
        }  catch (Exception e) {
            throw new RuntimeException("토스 응답 파싱 실패", e);
        }
    }
}
