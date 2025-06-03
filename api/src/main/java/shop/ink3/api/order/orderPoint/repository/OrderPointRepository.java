package shop.ink3.api.order.orderPoint.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.orderPoint.entity.OrderPoint;

public interface OrderPointRepository extends JpaRepository<OrderPoint, Long> {
    List<OrderPoint> findAllByOrderId(Long orderId);
}
