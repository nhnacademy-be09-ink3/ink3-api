package shop.ink3.api.coupon.policy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "coupon_policies")
@Builder
@Getter
public class CouponPolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    private int minimum_order_amount;
    private int discount_value;
    private int discount_percentage;
    private int maximum_discount_amount;
    private LocalDateTime createdAt;

    public void update(String name, DiscountType discountType, Integer minimum_order_amount, Integer discount_value, Integer maximum_discount_amount) {
        this.name = name;
        this.discountType = discountType;
        this.minimum_order_amount = minimum_order_amount;
        this.discount_value = discount_value;
        this.maximum_discount_amount = maximum_discount_amount;
    }
}
