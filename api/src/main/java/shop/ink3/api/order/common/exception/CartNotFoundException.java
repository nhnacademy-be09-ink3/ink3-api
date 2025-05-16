package shop.ink3.api.order.common.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(long cartId) {
        super("존재하지 않는 장바구니입니다 id: %d".formatted(cartId));
    }
}
