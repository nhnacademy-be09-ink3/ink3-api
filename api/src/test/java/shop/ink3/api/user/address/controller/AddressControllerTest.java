package shop.ink3.api.user.address.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.address.dto.AddressCreateRequest;
import shop.ink3.api.user.address.dto.AddressResponse;
import shop.ink3.api.user.address.dto.AddressUpdateRequest;
import shop.ink3.api.user.address.entity.Address;
import shop.ink3.api.user.address.exception.AddressNotFoundException;
import shop.ink3.api.user.address.service.AddressService;
import shop.ink3.api.user.user.exception.UserNotFoundException;

@WebMvcTest(AddressController.class)
class AddressControllerTest {
    @MockitoBean
    AddressService addressService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getAddress() throws Exception {
        Address address = Address.builder().id(1L).build();
        AddressResponse response = AddressResponse.from(address);
        when(addressService.getAddress(1L, 1L)).thenReturn(response);
        mockMvc.perform(get("/users/1/addresses/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getAddressWithNotFound() throws Exception {
        when(addressService.getAddress(1L, 1L)).thenThrow(new AddressNotFoundException(1L));
        mockMvc.perform(get("/users/1/addresses/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getAddresses() throws Exception {
        PageResponse<AddressResponse> response = new PageResponse<>(
                List.of(
                        AddressResponse.from(Address.builder().id(1L).build()),
                        AddressResponse.from(Address.builder().id(2L).build())
                ),
                0, 2, 2L, 1, false, false
        );
        when(addressService.getAddresses(anyLong(), any())).thenReturn(response);
        mockMvc.perform(get("/users/1/addresses")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[1].id").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andDo(print());
    }

    @Test
    void createAddress() throws Exception {
        AddressCreateRequest request = new AddressCreateRequest(
                "test",
                "11111",
                "test",
                "test",
                "test"
        );
        Address address = Address.builder()
                .id(1L)
                .name("test")
                .postalCode("11111")
                .defaultAddress("test")
                .detailAddress("test")
                .extraAddress("test")
                .isDefault(false)
                .build();
        AddressResponse response = AddressResponse.from(address);
        when(addressService.createAddress(1L, request)).thenReturn(response);
        mockMvc.perform(post("/users/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void createAddressWithUserNotFound() throws Exception {
        AddressCreateRequest request = new AddressCreateRequest(
                "test",
                "11111",
                "test",
                "test",
                "test"
        );
        when(addressService.createAddress(1L, request)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(post("/users/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void createAddressWithOverLimit() throws Exception {
        AddressCreateRequest request = new AddressCreateRequest(
                "test",
                "11111",
                "test",
                "test",
                "test"
        );
        when(addressService.createAddress(1L, request))
                .thenThrow(new IllegalStateException("You have exceeded the maximum number of addresses."));
        mockMvc.perform(post("/users/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void updateAddress() throws Exception {
        AddressUpdateRequest request = new AddressUpdateRequest(
                "test",
                "11111",
                "test",
                "test",
                "test"
        );
        when(addressService.updateAddress(1L, 1L, request))
                .thenReturn(AddressResponse.from(Address.builder().id(1L).build()));
        mockMvc.perform(put("/users/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void updateAddressWithNotFound() throws Exception {
        AddressUpdateRequest request = new AddressUpdateRequest(
                "test",
                "11111",
                "test",
                "test",
                "test"
        );
        when(addressService.updateAddress(anyLong(), anyLong(), any())).thenThrow(new AddressNotFoundException(1L));
        mockMvc.perform(put("/users/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void setDefaultAddress() throws Exception {
        doNothing().when(addressService).setDefaultAddress(1L, 1L);
        mockMvc.perform(patch("/users/1/addresses/1/default"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void setDefaultAddressWithNotFound() throws Exception {
        doThrow(new AddressNotFoundException(1L)).when(addressService).setDefaultAddress(1L, 1L);
        mockMvc.perform(patch("/users/1/addresses/1/default"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deleteAddress() throws Exception {
        doNothing().when(addressService).deleteAddress(1L, 1L);
        mockMvc.perform(delete("/users/1/addresses/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deleteAddressWithNotFound() throws Exception {
        doThrow(new AddressNotFoundException(1L)).when(addressService).deleteAddress(1L, 1L);
        mockMvc.perform(delete("/users/1/addresses/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
