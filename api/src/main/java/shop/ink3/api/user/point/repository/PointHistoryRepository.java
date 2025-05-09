package shop.ink3.api.user.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.ink3.api.user.point.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
