package shop.ink3.api.coupon.policy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.coupon.coupon.entity.Coupon;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.user.membership.entity.Membership;

public interface PolicyRepository extends JpaRepository<CouponPolicy, Long> {
    boolean existsByName(String name);
}
