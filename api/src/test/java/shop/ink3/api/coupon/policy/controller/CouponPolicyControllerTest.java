package shop.ink3.api.coupon.policy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

class CouponPolicyControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    // PolicyService를 Mockito로 목 생성
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        // Mockito.mock을 사용해 목 객체 생성
        policyService = Mockito.mock(PolicyService.class);

        // 컨트롤러 인스턴스를 직접 생성하여 목을 주입
        PolicyController controller = new PolicyController(policyService);

        // standaloneSetup으로 MockMvc 설정
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getPolicyById_success() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        // 1) Mockito가 getPolicyById(1L)을 호출하면 아래 응답객체를 리턴하도록 설정
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

        // 2) MockMvc로 GET /policies/1 요청 수행, JSON 결과 검증
        mockMvc.perform(get("/policies/1"))
                .andDo(print())
                .andExpect(status().isOk())
                // Controller가 CommonResponse<T> 형태로 래핑해 주므로
                // {"status":200,"data":{...PolicyResponse 필드...}}
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(1))
                .andExpect(jsonPath("$.data.policyName").value("TestPolicy"))
                .andExpect(jsonPath("$.data.minimumOrderAmount").value(15000))
                .andExpect(jsonPath("$.data.discountType").value("FIXED"))
                .andExpect(jsonPath("$.data.discountValue").value(3000))
                .andExpect(jsonPath("$.data.discountPercentage").value(0))
                .andExpect(jsonPath("$.data.maximumDiscountAmount").value(5000));
    }

    @Test
    void createPolicy_success() throws Exception {
        // 1) 테스트용 Request 객체 준비
        PolicyCreateRequest request = new PolicyCreateRequest(
                "NewPolicy",
                10000,
                DiscountType.RATE,
                0,
                10,
                2000
        );

        // 2) Service.createPolicy(...)가 호출되면, 아래 Response를 리턴하도록 설정
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

        // 3) MockMvc로 POST /policies 요청 수행, JSON 바디와 응답 검증
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.policyName").value("NewPolicy"))
                .andExpect(jsonPath("$.data.discountType").value("RATE"));
    }

    @Test
    void updatePolicy_success() throws Exception {
        // 1) 테스트용 Update Request 객체
        PolicyUpdateRequest request = new PolicyUpdateRequest(
                "UpdatedPolicy",
                DiscountType.FIXED,
                5000,
                1500,
                0,
                3000
        );

        // 2) Service.updatePolicy(...)가 호출되면, 아래 Response를 리턴하도록 설정
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

        // 3) MockMvc로 PUT /policies/3 요청 수행, JSON 바디와 응답 검증
        mockMvc.perform(put("/policies/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(3))
                .andExpect(jsonPath("$.data.policyName").value("UpdatedPolicy"))
                .andExpect(jsonPath("$.data.discountType").value("FIXED"))
                .andExpect(jsonPath("$.data.discountValue").value(1500));
    }

    @Test
    void deletePolicyById_success() throws Exception {
        // 1) Service.deletePolicyById(...)가 호출되면, 아래 Response를 리턴하도록 설정
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

        // 2) MockMvc로 DELETE /policies/5 요청 수행, 응답 검증
        mockMvc.perform(delete("/policies/5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.policyId").value(5))
                .andExpect(jsonPath("$.data.policyName").value("DeleteMe"));
    }
}
