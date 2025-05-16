package shop.ink3.api.order.order.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.coupon.store.entity.CouponStore;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;
import shop.ink3.api.user.user.entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderResponse {
    private Long id;
    private User user;
    private CouponStore couponStore;
    private String orderUUID;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private String ordererName;
    private String ordererPhone;

    public static OrderResponse from(Order order){
        return new OrderResponse(
                order.getId(),
                order.getUser(),
                order.getCouponStore(),
                order.getOrderUUID(),
                order.getStatus(),
                order.getOrderedAt(),
                order.getOrdererName(),
                order.getOrdererPhone()
        );
    }

    public static Order getOrder(OrderResponse response){
        return Order.builder()
                .id(response.id)
                .user(response.user)
                .couponStore(response.couponStore)
                .orderUUID(response.orderUUID)
                .status(response.status)
                .orderedAt(response.orderedAt)
                .ordererName(response.ordererName)
                .ordererPhone(response.ordererPhone)
                .build();
    }
}
