package shop.ink3.api.order.orderPoint.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.exception.OrderNotFoundException;
import shop.ink3.api.order.order.repository.OrderRepository;
import shop.ink3.api.order.orderPoint.entity.OrderPoint;
import shop.ink3.api.order.orderPoint.repository.OrderPointRepository;
import shop.ink3.api.user.point.history.entity.PointHistory;
import shop.ink3.api.user.point.history.repository.PointHistoryRepository;


@RequiredArgsConstructor
@Service
public class OrderPointService {
    private final OrderPointRepository orderPointRepository;
    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 생성
    public OrderPoint createOrderPoint(long orderId, PointHistory pointHistory) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return orderPointRepository.save(OrderPoint.builder()
                .order(order)
                .pointHistory(pointHistory)
                .build()
        );
    }

    // 주문에 대한 포인트 리스트 조회
    public List<OrderPoint> getOrderPoints(long orderId) {
        return orderPointRepository.findAllByOrderId(orderId);
    }
}
