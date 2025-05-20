package shop.ink3.api.payment.exception;

public class PaymentProcessorFailException extends RuntimeException{
    public PaymentProcessorFailException(String method,Exception e) {
        super(String.format("%s API 호출 실패.  Error : %s",method,e));
    }
}
