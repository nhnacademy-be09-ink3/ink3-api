package shop.ink3.api.order.order.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import shop.ink3.api.coupon.store.entity.CouponStore;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class OrderCreateRequest {
    private Long userId;
    private Long couponStoreId;
    @NotBlank
    @Length(max = 20)
    private String ordererName;
    @NotBlank
    @Length(max = 20)
    private String ordererPhone;
}
