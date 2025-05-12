package shop.ink3.api.order.refund.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.Order;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "refunds")
public class Refund {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false, length = 20)
    private String reason;

    @Column(nullable = false, length = 255)
    private String details;

    public void update(Refund refund) {
        this.details = refund.getDetails();
        this.reason = refund.getReason();
    }
}

