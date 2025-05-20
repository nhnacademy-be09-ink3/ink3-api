package shop.ink3.api.order.orderBook.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.orderBook.entity.OrderBook;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderBookResponse {
    private Long id;
    private Long orderId;
    private Long bookId;
    private Long packagingId;
    private Long couponId;
    private String bookName;
    private Integer bookSalePrice;
    private String packagingName;
    private Integer packagingPrice;
    private String couponName;
    private Integer discountPrice;
    private Integer price;
    private Integer quantity;


    public static OrderBookResponse from(OrderBook orderBook) {
        return new OrderBookResponse(
                orderBook.getId(),
                orderBook.getOrder().getId(),
                orderBook.getBook().getId(),
                orderBook.getPackaging() != null ? orderBook.getPackaging().getId() : null,
                orderBook.getCouponStore() != null ? orderBook.getCouponStore().getId() : null,
                orderBook.getBook().getTitle(),
                orderBook.getBook().getSalePrice(),
                orderBook.getPackaging() != null ? orderBook.getPackaging().getName() : null,
                orderBook.getPackaging() != null ? orderBook.getPackaging().getPrice() : null,
                orderBook.getCouponStore() != null ? orderBook.getCouponStore().getCoupon().getName() : null,
                orderBook.getDiscountPrice(),
                orderBook.getPrice(),
                orderBook.getQuantity()
        );
    }
}
