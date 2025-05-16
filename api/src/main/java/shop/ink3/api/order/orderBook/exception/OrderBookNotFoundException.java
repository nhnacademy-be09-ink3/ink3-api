package shop.ink3.api.order.orderBook.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class OrderBookNotFoundException extends NotFoundException {
    public OrderBookNotFoundException() {
        super("Available OrderBook not found.");
    }

    public OrderBookNotFoundException(long orderBookId) {
        super("OrderBook not found. ID: %d".formatted(orderBookId));
    }

}
