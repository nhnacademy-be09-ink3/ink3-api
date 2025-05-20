package shop.ink3.api.order.orderBook.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.orderBook.entity.OrderBook;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    Page<OrderBook> findByOrder_Id(long orderId, Pageable pageable);

    Optional<OrderBook> findByOrder_IdAndBook_Id(long orderId, long bookId);

    void deleteOrderBookListByOrder_Id(long orderId);
}
