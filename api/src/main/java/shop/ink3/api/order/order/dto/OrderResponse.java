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
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private String ordererName;
    private String ordererPhone;
    private CouponStore couponStore;

    public static OrderResponse from(Order order){
        return new OrderResponse(
                order.getId(),
                order.getUser(),
                order.getStatus(),
                order.getOrderedAt(),
                order.getOrdererName(),
                order.getOrdererPhone(),
                order.getCouponStore()
        );
    }

    public static Order getOrder(OrderResponse response){
        return Order.builder()
                .id(response.id)
                .user(response.user)
                .status(response.status)
                .orderedAt(response.orderedAt)
                .ordererName(response.ordererName)
                .ordererPhone(response.ordererPhone)
                .couponStore(response.couponStore)
                .build();
    }
}
