package shop.ink3.api.order.refund.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.refund.entity.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    Optional<Refund> findByOrder_Id(long OrderId);
    Page<Refund> findByOrder_UserId(long userId, Pageable pageable);
}
