package shop.ink3.api.order.orderBook.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.orderBook.entity.OrderBook;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderBookListResponse {
    private long orderId;
    private List<OrderBookResponse> orderBooks;

    public static OrderBookListResponse from(long orderId, List<OrderBook> orderBookList) {
        List<OrderBookResponse> orderBookResponseList = new ArrayList<>();
        for(OrderBook orderBook : orderBookList){
            orderBookResponseList.add(OrderBookResponse.from(orderBook));
        }

        return new OrderBookListResponse(
                orderId,
                orderBookResponseList
        );
    }
}
