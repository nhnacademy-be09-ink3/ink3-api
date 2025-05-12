package shop.ink3.api.order.refundPolicy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyUpdateRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "refund_policies")
public class RefundPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "return_deadline", nullable = false)
    private Integer returnDeadLine;

    @Column(name = "defect_return_deadline", nullable = false)
    private Integer defectReturnDeadLine;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void deactivate() {
        this.isAvailable = false;
    }

    public void activate() {
        this.isAvailable = true;
    }

    public void update(RefundPolicyUpdateRequest request) {
        this.name = request.getName();
        this.returnDeadLine = request.getReturnDeadLine();
        this.defectReturnDeadLine = request.getDefectReturnDeadLine();
    }
}
