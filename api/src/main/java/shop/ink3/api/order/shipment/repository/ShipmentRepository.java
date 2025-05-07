package shop.ink3.api.order.shipment.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.shipment.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Page<Shipment> findByOrder_UserId(long userId, Pageable pageable);

    Optional<Shipment> findByOrder_Id(long orderId);
}
