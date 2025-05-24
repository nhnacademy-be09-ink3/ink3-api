package shop.ink3.api.payment.exception;

public class PaymentCancelNotAllowedException extends RuntimeException{
    public PaymentCancelNotAllowedException() {
        super("주문 상태가 배송 전일 때만 결제 취소가 가능합니다.");
    }
}
