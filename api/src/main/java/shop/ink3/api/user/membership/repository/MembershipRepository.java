package shop.ink3.api.user.membership.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.membership.entity.Membership;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByIsDefault(boolean isDefault);

    boolean existsByIsDefault(boolean isDefault);
}
