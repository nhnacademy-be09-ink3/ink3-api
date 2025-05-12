package shop.ink3.api.order.refundPolicy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Getter
public class RefundPolicyUpdateRequest {
    @NotBlank
    @Length(max = 20)
    private String name;
    @NotBlank
    private Integer returnDeadLine;
    @NotBlank
    private Integer defectReturnDeadLine;
}
