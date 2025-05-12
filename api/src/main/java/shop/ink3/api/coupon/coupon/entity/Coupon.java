package shop.ink3.api.coupon.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;

@Entity
@Getter
@Setter
public class Coupon {

    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private TriggerType triggerType;

    @Column(name = "trigger_id")
    private Long triggerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "copon_policy_id")
    private CouponPolicy couponPolicy;

//    // === 유틸 메서드 ===
//    public Optional<BookCoupon> getBookTrigger(List<BookCoupon> allBookTriggers) {
//        if (triggerType == TriggerType.BOOK) {
//            return allBookTriggers.stream()
//                    .filter(bt -> bt.getCoupon().getId().equals(this.id))
//                    .findFirst();
//        }
//        return Optional.empty();
//    }

    // getter, setter ...
}

