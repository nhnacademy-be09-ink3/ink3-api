package shop.ink3.api.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(long orderId);

    void deleteByOrderId(long orderId);
}
