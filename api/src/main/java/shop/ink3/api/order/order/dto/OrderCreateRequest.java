package shop.ink3.api.order.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderCreateRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String ordererName;
    @NotBlank
    private String ordererPhone;
}
