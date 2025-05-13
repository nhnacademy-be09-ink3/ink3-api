package shop.ink3.api.order.order.repository;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser_Id(long userId, Pageable pageable);
    Page<Order> findAll(Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByUser_IdAndStatus(long userId, OrderStatus status, Pageable pageable);

    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Order> findByUser_IdAndOrderDateBetween(long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
