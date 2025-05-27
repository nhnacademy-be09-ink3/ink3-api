package shop.ink3.api.coupon.rabbitMq.message;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayCouponMessage {
    private List<Long> userIds;
    private Long couponId;
    private LocalDate issuedDate;
}
