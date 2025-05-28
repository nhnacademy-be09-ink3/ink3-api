package shop.ink3.api.order.refund.exception;

public class ReturnDeadlineExceededException extends RuntimeException {
    public ReturnDeadlineExceededException(){
        super("반품 가능 기간 초과 에러.");
    }
}
