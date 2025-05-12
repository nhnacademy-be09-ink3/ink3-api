package shop.ink3.api.coupon.store.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class CouponStore {
    @Id @GeneratedValue
    private Long coupon_store_id;

    private Long user_id;
    private Long coupon_id;
    private LocalDateTime created_at;
    private LocalDateTime used_at;
    private LocalDateTime valid_from;
    private LocalDateTime valid_until;
    private CouponStatus status;
}
