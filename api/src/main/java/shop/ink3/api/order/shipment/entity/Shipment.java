package shop.ink3.api.order.shipment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.order.entity.Order;
import shop.ink3.api.order.shipment.dto.ShipmentUpdateRequest;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "shipments")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private LocalDate preferredDeliveryDate;

    @Column(nullable = true)
    private LocalDateTime deliveredAt;

    @Column(nullable = false, length = 50)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String recipientPhone;

    @Column(nullable = false)
    private Integer postalCode;

    @Column(nullable = false, length = 100)
    private String defaultAddress;

    @Column(nullable = false, length = 100)
    private String detailAddress;

    @Column(nullable = false, length = 100)
    private String extraAddress;

    @Column(nullable = false)
    private Integer shippingFee;

    @Column(length = 20)
    private String shippingCode;


    public void updateDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void update(ShipmentUpdateRequest request) {
        this.recipientName = request.getRecipientName();
        this.recipientPhone = request.getRecipientPhone();
        this.postalCode = request.getPostalCode();
        this.defaultAddress = request.getDefaultAddress();
        this.detailAddress = request.getDetailAddress();
        this.extraAddress = request.getExtraAddress();
        this.shippingFee = request.getShippingFee();
        this.shippingCode = request.getShippingCode();
    }
}