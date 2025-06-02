package shop.ink3.api.payment.paymentUtil.resolver;

import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.ink3.api.payment.paymentUtil.parser.PaymentParser;

@Slf4j
@Component
public class PaymentResponseParserResolver {
    private final Map<String, PaymentParser> parserMap;

    public PaymentResponseParserResolver(Map<String, PaymentParser> parserMap) {
        this.parserMap = parserMap;
    }

    /**
     * Retrieves the {@link PaymentParser} associated with the specified payment type.
     *
     * @param paymentType the payment type identifier
     * @return the corresponding {@code PaymentParser} instance
     * @throws IllegalArgumentException if no parser is found for the given payment type
     */
    public PaymentParser getPaymentParser(String paymentType){
        PaymentParser paymentParser = parserMap.get(paymentType.toUpperCase());
        if(Objects.isNull(paymentParser)){
            //TODO 예외처리를 어떻게 할지.
            throw new IllegalArgumentException();
        }
        return paymentParser;
    }
}
