package shop.ink3.api.order.orderPoint.exception;

import shop.ink3.api.common.exception.NotFoundException;

public class OrderPointNotFoundException extends NotFoundException {
    public OrderPointNotFoundException() {
        super("OrderPoint not found.");
    }

    // 주문에 대한 포인트 정보가 없음.
    public OrderPointNotFoundException(long orderId) {
        super("OrderPoint not found. Order ID: %d".formatted(orderId));
    }

}
