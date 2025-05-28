package shop.ink3.api.order.guest.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.guest.entiity.GuestOrder;

public interface GuestOrderRepository extends JpaRepository<GuestOrder, Long> {
    Page<GuestOrder> findAllByEmailAndAndPassword(String email, String password, Pageable pageable);

    void deleteByOrderId(Long orderId);

    Optional<GuestOrder> findByOrderId(long orderId);
}
