package shop.ink3.api.user.address.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.address.dto.AddressCreateRequest;
import shop.ink3.api.user.address.dto.AddressResponse;
import shop.ink3.api.user.address.dto.AddressUpdateRequest;
import shop.ink3.api.user.address.entity.Address;
import shop.ink3.api.user.address.exception.AddressNotFoundException;
import shop.ink3.api.user.address.repository.AddressRepository;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    AddressRepository addressRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AddressService addressService;

    @Test
    void getAddress() {
        Address address = Address.builder()
                .id(1L)
                .name("test")
                .postalCode("11111")
                .defaultAddress("test")
                .detailAddress("test")
                .extraAddress("test")
                .isDefault(true)
                .build();
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        AddressResponse response = addressService.getAddress(1L, 1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(AddressResponse.from(address), response);
    }

    @Test
    void getAddressWithNotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenThrow(new AddressNotFoundException(1L));
        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.getAddress(1L, 1L));
    }

    @Test
    void getAddresses() {
        List<Address> addressList = List.of(
                Address.builder()
                        .id(1L)
                        .name("test1")
                        .postalCode("11111")
                        .defaultAddress("test1")
                        .detailAddress("test1")
                        .extraAddress("test1")
                        .isDefault(false)
                        .build(),
                Address.builder()
                        .id(2L)
                        .name("test2")
                        .postalCode("22222")
                        .defaultAddress("test2")
                        .detailAddress("test2")
                        .extraAddress("test2")
                        .isDefault(true)
                        .build()
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(addressList, pageable, addressList.size());

        when(addressRepository.findAllByUserId(1L, pageable)).thenReturn(addressPage);

        PageResponse<AddressResponse> response = addressService.getAddresses(1L, pageable);

        Assertions.assertEquals(2, response.content().size());
        Assertions.assertEquals(0, response.page());
        Assertions.assertEquals(10, response.size());
        Assertions.assertEquals(1, response.totalPages());
        Assertions.assertEquals(2, response.totalElements());
        Assertions.assertEquals("test1", response.content().get(0).name());
        Assertions.assertEquals("test2", response.content().get(1).name());
        verify(addressRepository, times(1)).findAllByUserId(1L, pageable);
    }

    @Test
    void createAddress() {
        User user = User.builder().id(1L).build();
        AddressCreateRequest request = new AddressCreateRequest(
                "test",
                "11111",
                "test",
                "test",
                "test"
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        AddressResponse response = addressService.createAddress(1L, request);
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(request.name(), response.name()),
                () -> Assertions.assertEquals(request.postalCode(), response.postalCode()),
                () -> Assertions.assertEquals(request.defaultAddress(), response.defaultAddress()),
                () -> Assertions.assertEquals(request.detailAddress(), response.detailAddress()),
                () -> Assertions.assertEquals(request.extraAddress(), response.extraAddress()),
                () -> Assertions.assertEquals(false, response.isDefault())
        );
    }

    @Test
    void updateAddress() {
        Address address = Address.builder()
                .id(1L)
                .name("test")
                .postalCode("11111")
                .defaultAddress("test")
                .detailAddress("test")
                .extraAddress("test")
                .isDefault(true)
                .build();
        AddressUpdateRequest request = new AddressUpdateRequest("new", "22222", "new", "new", "new");
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        AddressResponse response = addressService.updateAddress(1L, 1L, request);
        Assertions.assertNotNull(response);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1L, response.id()),
                () -> Assertions.assertEquals(request.name(), response.name()),
                () -> Assertions.assertEquals(request.postalCode(), response.postalCode()),
                () -> Assertions.assertEquals(request.defaultAddress(), response.defaultAddress()),
                () -> Assertions.assertEquals(request.detailAddress(), response.detailAddress()),
                () -> Assertions.assertEquals(request.extraAddress(), response.extraAddress()),
                () -> Assertions.assertEquals(true, response.isDefault())
        );
    }

    @Test
    void updateAddressWithNotFound() {
        AddressUpdateRequest request = new AddressUpdateRequest("new", "22222", "new", "new", "new");
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenThrow(new AddressNotFoundException(1L));
        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.updateAddress(1L, 1L, request));
    }

    @Test
    void setDefaultAddressWithDefaultAddress() {
        Address defaultAddress = Address.builder()
                .id(1L)
                .isDefault(true)
                .build();
        Address address = Address.builder()
                .id(2L)
                .isDefault(false)
                .build();
        when(addressRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(address));
        when(addressRepository.findByUserIdAndIsDefault(1L, true)).thenReturn(Optional.of(defaultAddress));
        when(addressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        addressService.setDefaultAddress(1L, 2L);
        Assertions.assertEquals(false, defaultAddress.getIsDefault());
        Assertions.assertEquals(true, address.getIsDefault());
        verify(addressRepository, times(1)).save(defaultAddress);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void setDefaultAddressWithDefaultAddressAndSameId() {
        Address address = Address.builder()
                .id(1L)
                .isDefault(true)
                .build();
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(addressRepository.findByUserIdAndIsDefault(1L, true)).thenReturn(Optional.of(address));
        when(addressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        addressService.setDefaultAddress(1L, 1L);
        Assertions.assertEquals(true, address.getIsDefault());
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void setDefaultAddressWithOutDefaultAddress() {
        Address address = Address.builder()
                .id(1L)
                .isDefault(false)
                .build();
        when(addressRepository.findByUserIdAndIsDefault(1L, true)).thenReturn(Optional.empty());
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(addressRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        addressService.setDefaultAddress(1L, 1L);
        Assertions.assertEquals(true, address.getIsDefault());
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void setDefaultAddressWithNotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenThrow(new AddressNotFoundException(1L));
        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.setDefaultAddress(1L, 1L));
    }

    @Test
    void deleteAddress() {
        Address address = Address.builder()
                .id(1L)
                .build();
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        addressService.deleteAddress(1L, 1L);
        verify(addressRepository).delete(address);
    }

    @Test
    void deleteAddressWithNotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenThrow(new AddressNotFoundException(1L));
        Assertions.assertThrows(AddressNotFoundException.class, () -> addressService.deleteAddress(1L, 1L));
    }
}
