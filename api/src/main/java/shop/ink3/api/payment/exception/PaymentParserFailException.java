package shop.ink3.api.payment.exception;

public class PaymentParserFailException extends RuntimeException{
    public PaymentParserFailException(String method,Exception e) {
        super(String.format("%s 응답 파싱 실패.  Error : %s",method,e));
    }
}
