package shop.ink3.api.order.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderUpdateRequest {
    @NotBlank
    @Length(max = 20)
    private String ordererName;
    @NotBlank
    @Length(max = 20)
    private String ordererPhone;
}
