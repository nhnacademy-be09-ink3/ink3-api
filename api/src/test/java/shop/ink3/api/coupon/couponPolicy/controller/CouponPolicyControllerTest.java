package shop.ink3.api.coupon.couponPolicy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.coupon.policy.controller.PolicyController;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.policy.service.PolicyService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
@TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
class PolicyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PolicyService policyService;

    @Test
    void getPolicyById_success() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        PolicyResponse sample = new PolicyResponse(
                1L,
                "TestPolicy",
                15000,
                DiscountType.FIXED,
                3000,
                0,
                5000,
                now
        );

        when(policyService.getPolicyById(1L)).thenReturn(sample);

        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(1))
                .andExpect(jsonPath("$.data.policyName").value("TestPolicy"))
                .andExpect(jsonPath("$.data.minimumOrderAmount").value(15000))
                .andExpect(jsonPath("$.data.discountType").value("FIXED"))
                .andExpect(jsonPath("$.data.discountValue").value(3000))
                .andExpect(jsonPath("$.data.discountPercentage").value(0))
                .andExpect(jsonPath("$.data.maximumDiscountAmount").value(5000))
                .andDo(print());
    }

    @Test
    void createPolicy_success() throws Exception {
        PolicyCreateRequest request = new PolicyCreateRequest(
                "NewPolicy",
                10000,
                DiscountType.RATE,
                0,
                10,
                2000
        );

        PolicyResponse created = new PolicyResponse(
                2L,
                "NewPolicy",
                10000,
                DiscountType.RATE,
                0,
                10,
                2000,
                LocalDateTime.now()
        );

        when(policyService.createPolicy(any(PolicyCreateRequest.class))).thenReturn(created);

        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.policyName").value("NewPolicy"))
                .andExpect(jsonPath("$.data.discountType").value("RATE"))
                .andDo(print());
    }

    @Test
    void updatePolicy_success() throws Exception {
        PolicyUpdateRequest request = new PolicyUpdateRequest(
                "UpdatedPolicy",
                DiscountType.FIXED,
                5000,
                1500,
                0,
                3000
        );

        // returned PolicyResponse after update
        PolicyResponse updated = new PolicyResponse(
                3L,
                "UpdatedPolicy",
                5000,
                DiscountType.FIXED,
                1500,
                0,
                3000,
                LocalDateTime.now()
        );

        when(policyService.updatePolicy(any(Long.class), any(PolicyUpdateRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/policies/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(3))
                .andExpect(jsonPath("$.data.policyName").value("UpdatedPolicy"))
                .andExpect(jsonPath("$.data.discountType").value("FIXED"))
                .andExpect(jsonPath("$.data.discountValue").value(1500))
                .andDo(print());
    }

    @Test
    void deletePolicyById_success() throws Exception {
        PolicyResponse deleted = new PolicyResponse(
                5L,
                "DeleteMe",
                2000,
                DiscountType.FIXED,
                500,
                0,
                1000,
                LocalDateTime.now()
        );

        when(policyService.deletePolicyById(5L)).thenReturn(deleted);

        mockMvc.perform(delete("/policies/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(5))
                .andExpect(jsonPath("$.data.policyName").value("DeleteMe"))
                .andDo(print());
    }
}
