package shop.ink3.api.coupon.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id
    private Long id;


    private String couponName;

    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;

    @Enumerated(EnumType.STRING)
    private IssueType issueType;

    private String couponCode;

    private LocalDateTime expiredDate;

    @ManyToOne
    @JoinColumn(name = "coupon_policy_id")
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
    public void update(String name, String code, Long policyId, TriggerType triggerType, IssueType issueType, String couponCode, LocalDateTime expiredDate) {
        this.couponName = name;
        this.couponCode = code;
        this.expiredDate = expiredDate;
        this.triggerType = triggerType;
        this.issueType = issueType;
    }

}
