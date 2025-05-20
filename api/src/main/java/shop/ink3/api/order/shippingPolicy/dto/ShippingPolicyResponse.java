package shop.ink3.api.order.shippingPolicy.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.ink3.api.order.shippingPolicy.entity.ShippingPolicy;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShippingPolicyResponse {
    private Long id;
    private String name;
    private Integer threshold;
    private Integer fee;
    private Boolean isAvailable;
    private LocalDateTime createdAt;


    public static ShippingPolicyResponse from(ShippingPolicy shippingPolicy) {
        return new ShippingPolicyResponse(
                shippingPolicy.getId(),
                shippingPolicy.getName(),
                shippingPolicy.getThreshold(),
                shippingPolicy.getFee(),
                shippingPolicy.getIsAvailable(),
                shippingPolicy.getCreatedAt()
        );
    }
}
