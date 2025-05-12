package shop.ink3.api.order.shipment.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Order order;

    @Column(name = "preferred_delivery_date", nullable = false)
    private LocalDate preferredDeliveryDate;

    @Column(name = "delivered_at", nullable = false)
    private LocalDateTime deliveredAt;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "postal_code", nullable = false)
    private Integer postalCode;

    @Column(name = "default_address", nullable = false, length = 100)
    private String defaultAddress;

    @Column(name = "detail_address", nullable = false, length = 100)
    private String detailAddress;

    @Column(name = "extra_address", nullable = false, length = 100)
    private String extraAddress;

    @Column(name = "shipping_fee", nullable = false)
    private Integer shippingFee;

    @Column(name = "shipping_code", length = 20)
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
