package shop.ink3.api.user.address.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressCreateRequest(
        @NotBlank String name,
        @NotBlank String postalCode,
        @NotBlank String defaultAddress,
        @NotBlank String detailAddress,
        String extraAddress
) {
}
