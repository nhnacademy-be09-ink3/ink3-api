package shop.ink3.api.order.orderBook.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.packaging.entity.Packaging;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderBookUpdateRequest {
    @Nullable
    private long packagingId;
    @Nullable
    private long couponStoreId;
    @NotNull
    private int price;
    @NotNull
    private int quantity;
}
