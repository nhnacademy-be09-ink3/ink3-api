package shop.ink3.api.order.guest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record GuestOrderCreateRequest(
        @NotNull
        Long orderId,
        @Email
        @NotBlank
        String email,
        @NotBlank
        String password
) {

}
