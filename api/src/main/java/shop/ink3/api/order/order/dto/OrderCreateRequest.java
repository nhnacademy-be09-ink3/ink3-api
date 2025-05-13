package shop.ink3.api.order.order.dto;

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
public class OrderCreateRequest {
    @NotBlank
    @Length(max = 20)
    private String ordererName;
    @NotBlank
    @Length(max = 20)
    private String ordererPhone;
}
