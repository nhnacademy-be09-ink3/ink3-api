package shop.ink3.api.order.refundPolicy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class RefundPolicyUpdateRequest {
    @NotBlank
    @Length(max = 20)
    private String name;
    @NotNull
    private Integer returnDeadLine;
    @NotNull
    private Integer defectReturnDeadLine;
    @NotNull
    private Integer refundShippingFee;
}
