package shop.ink3.api.coupon.couponPolicy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import shop.ink3.api.coupon.policy.controller.PolicyController;
import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.policy.exception.PolicyNotFoundException;
import shop.ink3.api.coupon.policy.service.PolicyService;

@WebMvcTest(PolicyController.class)
public class CouponPolicyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PolicyService policyService;

    @Test
    void getPolicyById() throws Exception {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .validDays(LocalDateTime.now().plusDays(30))
                .build();

        PolicyResponse response = PolicyResponse.from(policy, "쿠폰 정책 조회 완료");
        when(policyService.getPolicyById(1L)).thenReturn(response);

        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect((ResultMatcher) jsonPath("$.message").value("쿠폰 정책 조회 완료"))
                .andExpect((ResultMatcher) jsonPath("$.timestamp").exists())
                .andExpect((ResultMatcher) jsonPath("$.data.id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$.data.name").value("WELCOME"))
                .andExpect((ResultMatcher) jsonPath("$.data.discountType").value("RATE"))
                .andDo(print());
    }

    @Test
    void getPolicyById_notFound() throws Exception {
        when(policyService.getPolicyById(999L)).thenThrow(new PolicyNotFoundException("없는 쿠폰 정책"));

        mockMvc.perform(get("/policies/999"))
                .andExpect(status().isNotFound())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect((ResultMatcher) jsonPath("$.message").exists())
                .andExpect((ResultMatcher) jsonPath("$.data").isEmpty())
                .andDo(print());
    }

    @Test
    void createPolicy() throws Exception {
        PolicyCreateRequest request = new PolicyCreateRequest(
                "WELCOME",
                DiscountType.RATE,
                10000,
                10,
                5000,
                LocalDateTime.now().plusDays(30)
        );

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .validDays(request.valid_days())
                .build();

        PolicyResponse response = PolicyResponse.from(policy, "쿠폰 정책 생성 완료");

        when(policyService.createPolicy(any(PolicyCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect((ResultMatcher) jsonPath("$.data.name").value("WELCOME"))
                .andExpect((ResultMatcher) jsonPath("$.message").value("쿠폰 정책 생성 완료"))
                .andDo(print());
    }

    @Test
    void updatePolicy() throws Exception {
        PolicyUpdateRequest request = new PolicyUpdateRequest(
                "WELCOME",
                DiscountType.RATE,
                15000,
                15,
                6000,
                LocalDateTime.now().plusDays(20)
        );

        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME")
                .discountType(DiscountType.RATE)
                .discount_value(15)
                .minimum_order_amount(15000)
                .maximum_discount_amount(6000)
                .validDays(request.valid_days())
                .build();

        PolicyResponse response = PolicyResponse.from(policy, "쿠폰 정책이 수정되었습니다.");
        when(policyService.updatePolicy(any(PolicyUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.data.discountValue").value(15))
                .andExpect((ResultMatcher) jsonPath("$.message").value("쿠폰 정책이 수정되었습니다."))
                .andDo(print());
    }

    @Test
    void deletePolicyById() throws Exception {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .validDays(LocalDateTime.now().plusDays(30))
                .build();

        PolicyResponse response = PolicyResponse.from(policy, "쿠폰 정책이 삭제되었습니다.");
        when(policyService.deletePolicyById(1L)).thenReturn(response);

        mockMvc.perform(delete("/policies/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.message").value("쿠폰 정책이 삭제되었습니다."))
                .andDo(print());
    }

    @Test
    void deletePolicyByName() throws Exception {
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .validDays(LocalDateTime.now().plusDays(30))
                .build();

        PolicyResponse response = PolicyResponse.from(policy, "쿠폰 정책이 삭제되었습니다.");
        when(policyService.deletePolicyByName("WELCOME")).thenReturn(response);

        mockMvc.perform(delete("/policies/by-name/WELCOME"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.data.name").value("WELCOME"))
                .andExpect((ResultMatcher) jsonPath("$.message").value("쿠폰 정책이 삭제되었습니다."))
                .andDo(print());
    }

}
