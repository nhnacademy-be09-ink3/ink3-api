package shop.ink3.api.user.address.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByUserIdAndIsDefault(long userId, boolean isDefault);

    Page<Address> findAllByUserId(long userId, Pageable pageable);

    Optional<Address> findByIdAndUserId(long addressId, long userId);
}
