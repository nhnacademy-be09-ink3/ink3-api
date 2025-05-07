package shop.ink3.api.order.shippingPolicy.entity;

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
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyUpdateRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "shipping_policies")
public class ShippingPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "threshold", nullable = false)
    private Integer threshold;

    @Column(name = "fee", nullable = false)
    private Integer fee;

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


    public void update(ShippingPolicyUpdateRequest request) {
        this.name = request.getName();
        this.threshold = request.getThreshold();
        this.fee = request.getFee();
    }
}
