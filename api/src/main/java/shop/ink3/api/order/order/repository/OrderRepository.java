package shop.ink3.api.order.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
