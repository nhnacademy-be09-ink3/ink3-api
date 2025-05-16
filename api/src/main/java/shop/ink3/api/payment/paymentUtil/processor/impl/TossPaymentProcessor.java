package shop.ink3.api.payment.paymentUtil.processor.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shop.ink3.api.payment.dto.PaymentConfirmRequest;
import shop.ink3.api.payment.paymentUtil.processor.PaymentProcessor;

import java.util.Map;
import java.util.Base64;
import shop.ink3.api.payment.paymentUtil.client.PaymentClient;

@RequiredArgsConstructor
@Component("TOSS-PROCESSOR")
public class TossPaymentProcessor implements PaymentProcessor {

    @Value("${payment.toss.secret_key}")
    private String SECRET_KEY;
    private final PaymentClient tossPaymentClient;
    private final String PAYMENT_KEY = "paymentKey";
    private final String PAYMENT_AMOUNT = "amount";
    private final String PAYMENT_ORDER_ID = "orderId";

    @Override
    public String processPayment(PaymentConfirmRequest confirmRequest) {
        try {
            String basicAuthHeader = "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());

            Map<String, Object> body = Map.of(
                    PAYMENT_KEY, confirmRequest.paymentKey(),
                    PAYMENT_AMOUNT, confirmRequest.amount(),
                    PAYMENT_ORDER_ID, confirmRequest.orderUUID()
            );

            return tossPaymentClient.confirmPayment(basicAuthHeader, body);
        } catch (Exception e) {

            throw new RuntimeException("토스 API 호출 실패", e);
        }
    }
}
