package shop.ink3.api.order.orderBook.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.order.order.dto.OrderResponse;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.order.packaging.entity.Packaging;
import shop.ink3.api.review.entity.Review;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderBookResponse {
    private long id;
    private Order order;
    private Book book;
    private Packaging packaging;
    private CouponStore couponStore;
    private Integer price;
    private Integer quantity;


    public static OrderBookResponse from(OrderBook orderBook) {
        return new OrderBookResponse(
                orderBook.getId(),
                orderBook.getOrder(),
                orderBook.getBook(),
                orderBook.getPackaging(),
                orderBook.getCouponStore(),
                orderBook.getPrice(),
                orderBook.getQuantity()
        );
    }
}
