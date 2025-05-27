package shop.ink3.api.order.orderBook.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.orderBook.entity.OrderBook;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    Page<OrderBook> findAllByOrderId(long orderId, Pageable pageable);
    List<OrderBook> findAllByOrderId(long orderId);

    Optional<OrderBook> findByOrderIdAndBookId(long orderId, long bookId);

    void deleteOrderBookListByOrderId(long orderId);
}
