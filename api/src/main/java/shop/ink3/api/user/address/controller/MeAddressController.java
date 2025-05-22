package shop.ink3.api.user.address.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.address.dto.AddressCreateRequest;
import shop.ink3.api.user.address.dto.AddressResponse;
import shop.ink3.api.user.address.dto.AddressUpdateRequest;
import shop.ink3.api.user.address.service.AddressService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/me/addresses")
public class MeAddressController {
    private final AddressService addressService;

    @GetMapping("/{addressId}")
    public ResponseEntity<CommonResponse<AddressResponse>> getCurrentUserAddress(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long addressId
    ) {
        return ResponseEntity.ok(CommonResponse.success(addressService.getAddress(userId, addressId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<AddressResponse>>> getCurrentUserAddresses(
            @RequestHeader("X-User-Id") long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(CommonResponse.success(addressService.getAddresses(userId, pageable)));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<AddressResponse>> createCurrentUserAddress(
            @RequestHeader("X-User-Id") long userId,
            @RequestBody @Valid AddressCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.create(addressService.createAddress(userId, request)));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<CommonResponse<AddressResponse>> updateCurrentUserAddress(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long addressId,
            @RequestBody @Valid AddressUpdateRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.update(addressService.updateAddress(userId, addressId, request)));
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long addressId
    ) {
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteCurrentUserAddress(
            @RequestHeader("X-User-Id") long userId,
            @PathVariable long addressId
    ) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
