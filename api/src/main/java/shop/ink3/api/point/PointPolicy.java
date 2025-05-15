package shop.ink3.api.point;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_policies")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 20, nullable = false)
    private String name;
    
    @Column(name = "earn_point", nullable = false)
    private Integer earnPoint;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    /**
     * 포인트 정책 정보 업데이트
     * 
     * @param name 정책 이름
     * @param earnPoint 적립 포인트
     * @param isAvailable 활성화 여부
     * @return 업데이트된 포인트 정책
     */
    public PointPolicy update(String name, Integer earnPoint, Boolean isAvailable) {
        if (name != null) {
            this.name = name;
        }
        if (earnPoint != null) {
            this.earnPoint = earnPoint;
        }
        if (isAvailable != null) {
            this.isAvailable = isAvailable;
        }
        return this;
    }
    
    /**
     * 포인트 정책 활성화
     * 
     * @return 활성화된 포인트 정책
     */
    public PointPolicy activate() {
        this.isAvailable = true;
        return this;
    }
    
    /**
     * 포인트 정책 비활성화
     * 
     * @return 비활성화된 포인트 정책
     */
    public PointPolicy deactivate() {
        this.isAvailable = false;
        return this;
    }
}
