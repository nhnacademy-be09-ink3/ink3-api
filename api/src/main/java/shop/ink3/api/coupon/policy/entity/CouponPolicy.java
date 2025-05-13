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
@Getter
public class CouponPolicy {
    private Long id;

    private String name;

    private DiscountType discountType;

    private LocalDateTime validDays;

                       LocalDateTime valid_days) {
        this.name = name;
        this.discountType = discountType;
        this.validDays = valid_days;
    }
}
