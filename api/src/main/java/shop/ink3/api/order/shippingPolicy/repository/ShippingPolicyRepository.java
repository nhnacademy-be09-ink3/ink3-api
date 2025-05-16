package shop.ink3.api.order.shippingPolicy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.shippingPolicy.entity.ShippingPolicy;

public interface ShippingPolicyRepository extends JpaRepository<ShippingPolicy, Long> {
    Optional<ShippingPolicy> findByIsAvailableTrue();

}
