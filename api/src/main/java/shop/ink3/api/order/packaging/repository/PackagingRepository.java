package shop.ink3.api.order.packaging.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.order.packaging.entity.Packaging;

public interface PackagingRepository extends JpaRepository<Packaging, Long> {
    Page<Packaging> findByIsAvailableTrue(Pageable pageable);
}
