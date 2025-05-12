package shop.ink3.api.order.shippingPolicy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyResponse;
import shop.ink3.api.order.shippingPolicy.entity.ShippingPolicy;
import shop.ink3.api.order.shippingPolicy.exception.ShippingPolicyNotFoundException;
import shop.ink3.api.order.shippingPolicy.service.ShippingPolicyService;


@WebMvcTest(ShippingPolicyController.class)
class ShippingPolicyControllerTest {
    @MockitoBean
    ShippingPolicyService shippingPolicyService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("특정 배송 정책 ID 찾기 - 성공")
    void getShippingPolicy_성공() throws Exception {
        ShippingPolicy policy = ShippingPolicy.builder().id(1L).build();
        ShippingPolicyResponse shippingPolicyResponse = ShippingPolicyResponse.from(policy);
        when(shippingPolicyService.getShippingPolicy(1L)).thenReturn(shippingPolicyResponse);

        mockMvc.perform(get("/shippingPolicies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("특정 배송 정책 ID 찾기 - 실패")
    void getShippingPolicy_실패() throws Exception {
        when(shippingPolicyService.getShippingPolicy(1L)).thenThrow(new ShippingPolicyNotFoundException(1L));

        mockMvc.perform(get("/shippingPolicies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }


    @Test
    void getActivateShippingPolicy() {
    }

    @Test
    void getShippingPolicies() {
    }

    @Test
    void createShippingPolicy() {
    }

    @Test
    void updateShippingPolicy() {
    }

    @Test
    void activateShippingPolicy() {
    }

    @Test
    void deactivateShippingPolicy() {
    }

    @Test
    void deleteShippingPolicy() {
    }
}
