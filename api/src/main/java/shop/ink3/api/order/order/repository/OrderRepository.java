package shop.ink3.api.order.order.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserId(long userId, Pageable pageable);
    Page<Order> findAll(Pageable pageable);
    Order findByPointHistoryId(long pointHistoryId);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findAllByUserIdAndStatus(long userId, OrderStatus status, Pageable pageable);

    Page<Order> findAllByOrderedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Order> findAllByUserIdAndOrderedAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
