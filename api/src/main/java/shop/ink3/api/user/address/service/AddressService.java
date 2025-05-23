package shop.ink3.api.user.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.address.dto.AddressCreateRequest;
import shop.ink3.api.user.address.dto.AddressResponse;
import shop.ink3.api.user.address.dto.AddressUpdateRequest;
import shop.ink3.api.user.address.entity.Address;
import shop.ink3.api.user.address.exception.AddressNotFoundException;
import shop.ink3.api.user.address.repository.AddressRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AddressResponse getAddress(long userId, long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        return AddressResponse.from(address);
    }

    @Transactional(readOnly = true)
    public PageResponse<AddressResponse> getAddresses(long userId, Pageable pageable) {
        Page<Address> addresses = addressRepository.findAllByUserId(userId, pageable);
        return PageResponse.from(addresses.map(AddressResponse::from));
    }

    public AddressResponse createAddress(long userId, AddressCreateRequest request) {
        if (addressRepository.countByUserId(userId) >= 10) {
            throw new IllegalStateException("You have exceeded the maximum number of addresses.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Address address = Address.builder()
                .name(request.name())
                .user(user)
                .postalCode(request.postalCode())
                .defaultAddress(request.defaultAddress())
                .detailAddress(request.detailAddress())
                .extraAddress(request.extraAddress())
                .isDefault(false)
                .build();
        return AddressResponse.from(addressRepository.save(address));
    }

    public AddressResponse updateAddress(long userId, long addressId, AddressUpdateRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        address.update(
                request.name(),
                request.postalCode(),
                request.defaultAddress(),
                request.detailAddress(),
                request.extraAddress()
        );
        return AddressResponse.from(addressRepository.save(address));
    }

    public void setDefaultAddress(long userId, long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        addressRepository.findByUserIdAndIsDefault(userId, true).ifPresent(current -> {
            if (!current.getId().equals(addressId)) {
                current.unmarkAsDefault();
                addressRepository.save(current);
            }
        });
        address.markAsDefault();
        addressRepository.save(address);
    }

    public void deleteAddress(long userId, long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));
        addressRepository.delete(address);
    }
}
