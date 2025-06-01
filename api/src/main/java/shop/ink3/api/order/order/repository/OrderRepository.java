package shop.ink3.api.order.order.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.ink3.api.order.order.dto.OrderWithDetailsResponse;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.order.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // querydsl은 limit 사용에 제한이 있으며 전체적으로 querydsl을 프로젝트에서 사용안함 + 쿼리 성능 향상이 목표이기 때문에 native query 사용
    @Query(
            value = """
        SELECT 
            o.id AS id,
            o.order_uuid AS orderUUID,
            o.status AS status,
            o.ordered_at AS orderedAt,
            o.orderer_name AS ordererName,
            o.orderer_phone AS ordererPhone,
            p.payment_amount AS paymentAmount,
            rep.book_name AS representativeBookName,
            rep.thumbnail_url AS representativeThumbnailUrl,
            (
                SELECT COUNT(DISTINCT ob.book_id) 
                FROM order_books ob 
                WHERE ob.order_id = o.id
            ) AS bookTypeCount
        FROM orders o
        JOIN payments p ON p.order_id = o.id
        JOIN (
            SELECT ob1.id, ob1.order_id, b.title AS book_name, b.thumbnail_url
            FROM order_books ob1
            JOIN books b ON ob1.book_id = b.id
            WHERE ob1.id IN (
                SELECT MIN(ob2.id)
                FROM order_books ob2
                GROUP BY ob2.order_id
            )
        ) rep ON rep.order_id = o.id
        WHERE o.user_id = :userId
        """,
            countQuery = "SELECT COUNT(*) FROM orders o WHERE o.user_id = :userId",
            nativeQuery = true
    )
    Page<OrderWithDetailsResponse> findAllByUserId(long userId, Pageable pageable);


    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findAllByUserIdAndStatus(long userId, OrderStatus status, Pageable pageable);

    Page<Order> findAllByOrderedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<Order> findAllByUserIdAndOrderedAtBetween(long userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
