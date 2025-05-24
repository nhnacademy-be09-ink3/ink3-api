package shop.ink3.api.order.guestOrder.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.guestOrder.entiity.GuestOrder;

public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {
    Page<GuestOrder> findByEmailAndAndPassword(String email, String password, Pageable pageable);

    void deleteByOrder_Id(long orderId);

    Optional<GuestOrder> findByOrder_Id(long orderId);
}
