package shop.ink3.api.coupon.policy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CouponPolicy {
    @Id @GeneratedValue
    private Long id;

    private String name;

    private DiscountType discountType;

    private int minimum_order_amount;
    private int discount_value;
    private int discount_percentage;
    private int maximum_discount_amount;
    private LocalDateTime validDays;

    public void update(String name, DiscountType discountType, Integer minimum_order_amount, Integer discount_value, Integer maximum_discount_amount,
                       LocalDateTime valid_days) {
        this.name = name;
        this.discountType = discountType;
        this.minimum_order_amount = minimum_order_amount;
        this.discount_value = discount_value;
        this.maximum_discount_amount = maximum_discount_amount;
        this.validDays = valid_days;
    }
}
