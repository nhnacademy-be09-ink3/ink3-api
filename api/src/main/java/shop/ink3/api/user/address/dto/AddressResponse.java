package shop.ink3.api.user.address.dto;

import shop.ink3.api.user.address.entity.Address;

public record AddressResponse(
        Long id,
        String name,
        String postalCode,
        String defaultAddress,
        String detailAddress,
        String extraAddress,
        Boolean isDefault
) {
    public static AddressResponse from(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getName(),
                address.getPostalCode(),
                address.getDefaultAddress(),
                address.getDetailAddress(),
                address.getExtraAddress(),
                address.getIsDefault()
        );
    }
}
