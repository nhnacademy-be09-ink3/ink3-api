package shop.ink3.api.coupon.policy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;

public interface PolicyRepository extends JpaRepository<CouponPolicy, Long> {
    Optional<CouponPolicy> findByName(String name);
    boolean existsByName(String name);
}
