package shop.ink3.api.order.refundPolicy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {
    public Page<RefundPolicy> findByIsAvailableTrue(Pageable pageable);
}
