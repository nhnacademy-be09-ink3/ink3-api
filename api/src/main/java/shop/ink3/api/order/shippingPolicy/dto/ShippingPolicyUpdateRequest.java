package shop.ink3.api.order.shippingPolicy.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShippingPolicyUpdateRequest {
    @NotBlank
    @Length(max = 20)
    private String name;
    @NotBlank
    private Integer threshold;
    @NotBlank
    private Integer fee;
}
