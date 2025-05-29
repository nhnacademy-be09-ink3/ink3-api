package shop.ink3.api.payment.paymentUtil.resolver;

import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.ink3.api.payment.paymentUtil.processor.PaymentProcessor;

@Slf4j
@Component
public class PaymentProcessorResolver {
    private final Map<String, PaymentProcessor> processorMap;


    public PaymentProcessorResolver(Map<String, PaymentProcessor> processorMap) {
        this.processorMap = processorMap;
    }

    public PaymentProcessor getPaymentProcessor(String paymentType){
        PaymentProcessor processor = processorMap.get(paymentType.toUpperCase());
        if(Objects.isNull(processor)){
            //TODO 예외처리를 어떻게 할지.
            throw new IllegalArgumentException();
        }
        return processor;
    }
}
