package shop.ink3.api.coupon.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;

@Entity
@Getter
public class Coupon {
    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;

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

}
