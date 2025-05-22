package shop.ink3.api.coupon.couponPolicy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.coupon.policy.controller.PolicyController;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.policy.service.PolicyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class CouponPolicyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PolicyService policyService;

    @Test
    void getPolicyById() throws Exception {
        PolicyResponse response = new PolicyResponse(1L, "TestPolicy", DiscountType.FIXED, 1000, 0, LocalDateTime.now(),"쿠폰 정책 조회 완료");
        when(policyService.getPolicyById(1L)).thenReturn(response);

        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(1L))
                .andExpect(jsonPath("$.data.policyName").value("TestPolicy"))
                .andDo(print());
    }

    @Test
    void getPolicyByName() throws Exception {
        PolicyResponse response = new PolicyResponse(1L, "TestPolicy", DiscountType.FIXED, 1000, 0, LocalDateTime.now(),"쿠폰 정책 생성 완료");
        when(policyService.getPolicyByName("TestPolicy")).thenReturn(response);

        mockMvc.perform(get("/policies/by-name/TestPolicy"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyName").value("TestPolicy"))
                .andDo(print());
    }

    @Test
    void createPolicy() throws Exception {
        PolicyCreateRequest request = new PolicyCreateRequest("NewPolicy", DiscountType.RATE, 10000,0,10,0,LocalDateTime.now());
        PolicyResponse response = new PolicyResponse(1L, "NewPolicy", DiscountType.RATE, 0, 10, LocalDateTime.now(),"쿠폰 정책 생성 완료");
        when(policyService.createPolicy(any())).thenReturn(response);

        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.policyName").value("NewPolicy"))
                .andDo(print());
    }

//    @Test
//    void updatePolicy() throws Exception {
//        PolicyUpdateRequest request = new PolicyUpdateRequest("updatedPolicy", DiscountType.FIXED, 10000,500,0,0);
//        PolicyResponse response = new PolicyResponse(1L, "UpdatedPolicy", DiscountType.FIXED, 500, 0, LocalDateTime.now(),"쿠폰 정책이 수정되었습니다.");
//        when(policyService.updatePolicy(any())).thenReturn(response);
//
//        mockMvc.perform(put("/api/policies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
//                .andExpect(jsonPath("$.data.policyName").value("UpdatedPolicy"))
//                .andDo(print());
//    }

    @Test
    void deletePolicyById() throws Exception {
        PolicyResponse response = new PolicyResponse(1L, "DeletedPolicy", DiscountType.FIXED, 100, 0, LocalDateTime.now(), "쿠폰 정책 생성 완료");
        when(policyService.deletePolicyById(1L)).thenReturn(response);

        mockMvc.perform(delete("/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(1L))
                .andDo(print());
    }

}
