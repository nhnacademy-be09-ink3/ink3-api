package shop.ink3.api.order.shippingPolicy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShippingPolicyCreateRequest {
    @NotBlank
    @Length(max = 20)
    private String name;
    @NotBlank
    private Integer threshold;
    @NotBlank
    private Integer fee;
}
