package shop.ink3.api.user.address.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressUpdateRequest(
        @NotBlank String name,
        @NotBlank String postalCode,
        @NotBlank String defaultAddress,
        @NotBlank String detailAddress,
        String extraAddress
) {
}
