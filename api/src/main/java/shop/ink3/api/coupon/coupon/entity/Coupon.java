package shop.ink3.api.coupon.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "coupons")
public class Coupon {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TriggerType triggerType;

//    @Column(nullable = false)
//    private CouponPolicy couponPolicy;

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
