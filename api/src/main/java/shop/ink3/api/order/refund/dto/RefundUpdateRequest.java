package shop.ink3.api.order.refund.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefundUpdateRequest {
    @NotBlank
    @Length(max = 20)
    private String reason;
    @NotBlank
    @Length(max = 255)
    private String details;
}
