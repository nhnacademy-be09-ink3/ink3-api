package shop.ink3.api.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.ink3.api.point.PointPolicy;

@Repository
public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    
    /**
     * 활성화된 포인트 정책 조회 메서드
     */
    PointPolicy findByIsAvailableTrue();
    
    /**
     * 정책 이름으로 포인트 정책 조회 메서드
     */
    PointPolicy findByName(String name);
}
