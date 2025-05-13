package shop.ink3.api.coupon.store.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class CouponStore {

    private CouponStatus status;
}
