package shop.ink3.api.order.orderBook.exception;

public class OrderBookNotFoundException extends RuntimeException {
    public OrderBookNotFoundException(long orderBookId) {
        super("존재하지 않는 주문 도서입니다. id: %d".formatted(orderBookId));
    }
}
