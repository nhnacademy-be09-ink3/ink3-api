package shop.ink3.api.order.order.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.OrderStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderResponse {
    private Long id;
    // fix : 사용자ID 연결 필요. 다대일
    private Long userId;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private String ordererName;
    private String ordererPhone;
    // fix : 쿠폰 보관함ID 연결 필요 다대일
    private Long couponStoreId;
}
