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
@Builder
@Getter
@Table(name = "coupon_policies")
public class CouponPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    private int minimumOrderAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    private int discountValue;
    private int discountPercentage;
    private int maximumDiscountAmount;
    private LocalDateTime createdAt;

    public void update(String name, DiscountType discountType, Integer minimumOrderAmount, Integer discountValue, Integer discountPercentage,Integer maximumDiscountAmount) {
        this.name = name;
        this.discountType = discountType;
        this.minimumOrderAmount = minimumOrderAmount;
        this.discountValue = discountValue;
        this.discountPercentage = discountPercentage;
        this.maximumDiscountAmount = maximumDiscountAmount;
    }
}
