package shop.ink3.api.order.orderBook.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderBookCreateRequest {
    @NotNull
    private Long bookId;
    @Nullable
    private Long packagingId;
    @Nullable
    private Long couponStoreId;
    @NotNull
    private Integer price;
    @NotNull
    private Integer quantity;
}
