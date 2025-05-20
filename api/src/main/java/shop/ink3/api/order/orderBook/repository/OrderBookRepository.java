package shop.ink3.api.order.orderBook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.orderBook.entity.OrderBook;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
}
