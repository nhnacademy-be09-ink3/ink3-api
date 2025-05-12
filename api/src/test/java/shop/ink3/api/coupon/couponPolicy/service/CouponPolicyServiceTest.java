package shop.ink3.api.coupon.couponPolicy.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import shop.ink3.api.coupon.policy.dto.PolicyResponse;
import shop.ink3.api.coupon.policy.entity.CouponPolicy;
import shop.ink3.api.coupon.policy.entity.DiscountType;
import shop.ink3.api.coupon.policy.repository.PolicyRepository;
import shop.ink3.api.coupon.policy.service.PolicyService;

@ExtendWith(MockitoExtension.class)
public class CouponPolicyServiceTest {
    @Mock
    PolicyRepository policyRepository;

    @InjectMocks
    PolicyService policyService;

    @Test
    void getPolicyById() throws Exception {
        // given
        CouponPolicy policy = CouponPolicy.builder()
                .id(1L)
                .name("WELCOME10")
                .discountType(DiscountType.RATE)
                .discount_value(10)
                .minimum_order_amount(10000)
                .maximum_discount_amount(5000)
                .validDays(LocalDateTime.now().plusDays(30))
                .build();

        PolicyResponse response = PolicyResponse.from(policy, "쿠폰 정책 조회 완료");

        when(policyService.getPolicyById(1L)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("쿠폰 정책 조회 완료"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("WELCOME10"))
                .andExpect(jsonPath("$.data.discountType").value("RATE"))
                .andExpect(jsonPath("$.data.discountValue").value(10))
                .andExpect(jsonPath("$.data.minimumOrderAmount").value(10000))
                .andExpect(jsonPath("$.data.maximumDiscountAmount").value(5000))
                .andExpect(jsonPath("$.data.validDays").exists())
                .andDo(print());
    }
}
