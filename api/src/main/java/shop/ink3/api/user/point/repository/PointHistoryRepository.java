package shop.ink3.api.user.point.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.point.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findAllByUserId(long userId, Pageable pageable);

    Optional<PointHistory> findByIdAndUserId(long userId, long pointHistoryId);

    boolean existsByOriginId(long originId);
}
