//package shop.ink3.api.coupon.policy.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import shop.ink3.api.coupon.policy.dto.PolicyCreateRequest;
//import shop.ink3.api.coupon.policy.dto.PolicyResponse;
//import shop.ink3.api.coupon.policy.dto.PolicyUpdateRequest;
//import shop.ink3.api.coupon.policy.entity.CouponPolicy;
//import shop.ink3.api.coupon.policy.entity.DiscountType;
//import shop.ink3.api.coupon.policy.service.PolicyService;
//
//
//@WebMvcTest(PolicyController.class)
//public class CouponPolicyControllerTest {
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @MockitoBean
//    PolicyService policyService;
//
//    private CouponPolicy samplePolicy(){
//        return CouponPolicy.builder()
//                .id(1L)
//                .name("test")
//                .minimumOrderAmount(1000)
//                .discountType(DiscountType.FIXED)
//                .discountValue(500)
//                .discountPercentage(0)
//                .maximumDiscountAmount(0)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    @DisplayName("POST /policies - 생성")
//    void createPolicy() throws Exception {
//        // given
//        PolicyCreateRequest req = new PolicyCreateRequest(
//                "test", 2000,
//                DiscountType.RATE, 0,
//                10, 1000
//        );
//        CouponPolicy saved = CouponPolicy.builder()
//                .id(2L)
//                .name(req.name())
//                .minimumOrderAmount(req.minimumOrderAmount())
//                .discountType(req.discountType())
//                .discountPercentage(req.discountPercentage())
//                .maximumDiscountAmount(req.maximumDiscountAmount())
//                .createdAt(LocalDateTime.now())
//                .build();
//        PolicyResponse resp = PolicyResponse.from(saved, "쿠폰 정책 생성 완료");
//        when(policyService.createPolicy(any(PolicyCreateRequest.class))).thenReturn(resp);
//
//        // when & then
//        mockMvc.perform(post("/policies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.data.policyId").value(2))
//                .andExpect(jsonPath("$.data.policyName").value("test"))
//                .andExpect(jsonPath("$.data.message").value("쿠폰 정책 생성 완료"));
//    }
//
//    @Test
//    @DisplayName("GET /policies - 전체 조회")
//    void getPolicy() throws Exception {
//        CouponPolicy p = samplePolicy();
//        PolicyResponse resp = PolicyResponse.from(p, "정책 조회 성공");
//        when(policyService.getPolicy()).thenReturn(List.of(resp));
//
//        // when & then
//        mockMvc.perform(get("/policies"))
//                .andExpect(status().isOk())
//                // CommonResponse 의 데이터 필드가 "data" 라고 가정
//                .andExpect(jsonPath("$.data").isArray())
//                .andExpect(jsonPath("$.data[0].policyId").value(1))
//                .andExpect(jsonPath("$.data[0].policyName").value("test"))
//                .andExpect(jsonPath("$.data[0].message").value("정책 조회 성공"));
//
//    }
//
//    @Test
//    @DisplayName("GET /policies/{id} - 단건조회")
//    void getPolicyById() throws Exception {
//        // given
//        CouponPolicy p = samplePolicy();
//        PolicyResponse resp = PolicyResponse.from(p, "쿠폰 정책 조회 완료");
//        when(policyService.getPolicyById(1L)).thenReturn(resp);
//
//        // when & then
//        mockMvc.perform(get("/policies/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.policyId").value(1))
//                .andExpect(jsonPath("$.data.policyName").value("test"))
//                .andExpect(jsonPath("$.data.message").value("쿠폰 정책 조회 완료"));
//    }
//
//    @Test
//    @DisplayName("PUT /policies/{id} - 수정")
//    void updatePolicy() throws Exception {
//        PolicyUpdateRequest req = new PolicyUpdateRequest(
//                "update", DiscountType.FIXED,
//                3000, 300,
//                0, 0
//        );
//        CouponPolicy existing = samplePolicy();
//        existing.update(
//                req.name(),
//                req.discountType(),
//                req.minimumOrderAmount(),
//                req.discountValue(),
//                req.discountPercentage(),
//                req.maximumDiscountAmount()
//        );
//        PolicyResponse resp = PolicyResponse.from(existing, "쿠폰 정책이 수정되었습니다.");
//        when(policyService.updatePolicy(eq(1L), any(PolicyUpdateRequest.class))).thenReturn(resp);
//
//        mockMvc.perform(put("/policies/{id}", 1L)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.policyId").value(1))
//                .andExpect(jsonPath("$.data.policyName").value("update"))
//                .andExpect(jsonPath("$.data.message").value("쿠폰 정책이 수정되었습니다."));
//    }
//
//    @Test
//    @DisplayName("DELETE /policies/{id} - 삭제")
//    void deletePolicy() throws Exception {
//
//        CouponPolicy p = samplePolicy();
//        PolicyResponse resp = PolicyResponse.from(p, "쿠폰 정책이 삭제되었습니다.");
//        when(policyService.deletePolicyById(1L)).thenReturn(resp);
//
//        mockMvc.perform(delete("/policies/{id}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.policyId").value(1))
//                .andExpect(jsonPath("$.data.message").value("쿠폰 정책이 삭제되었습니다."));
//    }
//}
