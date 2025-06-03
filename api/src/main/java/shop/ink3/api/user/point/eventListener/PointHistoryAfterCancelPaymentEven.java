package shop.ink3.api.user.point.eventListener;

import java.util.List;
import shop.ink3.api.order.orderPoint.entity.OrderPoint;

public record PointHistoryAfterCancelPaymentEven (long orderId, List<OrderPoint> orderPoints){
}
