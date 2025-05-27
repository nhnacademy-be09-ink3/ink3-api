package shop.ink3.api.order.guestOrder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record GuestOrderCreateRequest(
        @NotNull
        Long orderId,
        @NotBlank
        String email,
        @NotBlank
        String password
) {

}
