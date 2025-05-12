package shop.ink3.api.coupon.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import shop.ink3.api.coupon.coupon.entity.Coupon;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "coupon_stores")
public class CouponStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupon_store_id;

//    @Column(nullable = false)
//    private User user_id;

//    @Column(nullable = false)
//    private Coupon coupon_id;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime used_at;

    @Column(nullable = false)
    private LocalDateTime valid_from;

    @Column(nullable = false)
    private LocalDateTime valid_until;

    @Column(nullable = false)
    private CouponStatus status;
}
