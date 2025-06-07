package shop.ink3.api.order.orderPoint.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.ink3.api.order.orderPoint.entity.OrderPoint;

public interface OrderPointRepository extends JpaRepository<OrderPoint, Long> {
    @Query("SELECT op FROM OrderPoint op JOIN FETCH op.pointHistory WHERE op.order.id = :orderId")
    List<OrderPoint> findAllByOrderId(Long orderId);
}
