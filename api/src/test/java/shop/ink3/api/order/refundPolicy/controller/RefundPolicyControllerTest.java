package shop.ink3.api.order.refundPolicy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyCreateRequest;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyResponse;
import shop.ink3.api.order.refundPolicy.dto.RefundPolicyUpdateRequest;
import shop.ink3.api.order.refundPolicy.entity.RefundPolicy;
import shop.ink3.api.order.refundPolicy.exception.RefundPolicyNotFoundException;
import shop.ink3.api.order.refundPolicy.service.RefundPolicyService;

@WebMvcTest(RefundPolicyController.class)
class RefundPolicyControllerTest {

    @MockitoBean
    RefundPolicyService refundPolicyService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("반품 정책 단건 조회 - 성공")
    void getRefundPolicy_성공() throws Exception {
        // given
        RefundPolicy policy = RefundPolicy.builder().id(1L).name("테스트").build();
        RefundPolicyResponse response = RefundPolicyResponse.from(policy);
        when(refundPolicyService.getRefundPolicy(1L)).thenReturn(response);

        // when, then
        mockMvc.perform(get("/refundPolicies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 단건 조회 - 실패")
    void getRefundPolicy_실패() throws Exception {
        // given
        when(refundPolicyService.getRefundPolicy(1L)).thenThrow(new RefundPolicyNotFoundException(1L));

        // when, then
        mockMvc.perform(get("/refundPolicies/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 목록 조회 - 성공")
    void getRefundPolicyList_성공() throws Exception {
        // given
        PageResponse<RefundPolicyResponse> pageResponse = new PageResponse<>(
                List.of(
                        RefundPolicyResponse.from(RefundPolicy.builder().id(1L).name("정책1").build()),
                        RefundPolicyResponse.from(RefundPolicy.builder().id(2L).name("정책2").build())
                ),
                0, 2, 2L, 1, false, false
        );
        when(refundPolicyService.getRefundPolicyList(any())).thenReturn(pageResponse);

        // when, then
        mockMvc.perform(get("/refundPolicies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
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
    @DisplayName("환불 정책 목록 조회 - 실패")
    void getRefundPolicyList_실패() throws Exception {
        // given
        when(refundPolicyService.getRefundPolicyList(any())).thenThrow(new RefundPolicyNotFoundException());

        // when, then
        mockMvc.perform(get("/refundPolicies"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("활성화된 반품 정책 목록 조회 - 성공")
    void getActivateRefundPolicyList_성공() throws Exception {
        // given
        RefundPolicyResponse refundPolicyResponse = RefundPolicyResponse.from(RefundPolicy.builder().id(1L).name("테스트1").build());
        when(refundPolicyService.getAvailableRefundPolicy()).thenReturn(refundPolicyResponse);

        // when, then
        mockMvc.perform(get("/refundPolicies/activate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("활성화된 반품 정책 목록 조회 - 실패")
    void getActivateRefundPolicyList_실패() throws Exception {
        // given
        when(refundPolicyService.getAvailableRefundPolicy()).thenThrow(new RefundPolicyNotFoundException());

        // when, then
        mockMvc.perform(get("/refundPolicies/activate"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 생성 - 성공")
    void createRefundPolicy_성공() throws Exception {
        // given
        RefundPolicyCreateRequest request = new RefundPolicyCreateRequest("테스트 정책", 7, 30,3000);
        RefundPolicy policy = RefundPolicy.builder().id(1L).name("테스트 정책").build();
        RefundPolicyResponse response = RefundPolicyResponse.from(policy);
        when(refundPolicyService.createRefundPolicy(any())).thenReturn(response);

        // when, then
        mockMvc.perform(post("/refundPolicies")
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
    @DisplayName("반품 정책 생성 - 실패")
    void createRefundPolicy_실패() throws Exception {
        // given
        RefundPolicyCreateRequest request = new RefundPolicyCreateRequest("테스트 정책", 7, 30,3000);
        when(refundPolicyService.createRefundPolicy(any())).thenThrow(new RefundPolicyNotFoundException(1L));

        // when, then
        mockMvc.perform(post("/refundPolicies")
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
    @DisplayName("반품 정책 수정 - 성공")
    void updateRefundPolicy_성공() throws Exception {
        // given
        RefundPolicyUpdateRequest request = new RefundPolicyUpdateRequest("변경전", 10, 15,3000);
        RefundPolicy policy = RefundPolicy.builder().id(1L).name("변경후").build();
        RefundPolicyResponse response = RefundPolicyResponse.from(policy);
        when(refundPolicyService.updateRefundPolicy(anyLong(), any())).thenReturn(response);

        // when, then
        mockMvc.perform(put("/refundPolicies/1")
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
    @DisplayName("반품 정책 수정 - 실패")
    void updateRefundPolicy_실패() throws Exception {
        // given
        RefundPolicyUpdateRequest request = new RefundPolicyUpdateRequest("수정된 정책", 10, 15,3000);
        when(refundPolicyService.updateRefundPolicy(anyLong(), any())).thenThrow(new RefundPolicyNotFoundException(1L));

        // when, then
        mockMvc.perform(put("/refundPolicies/1")
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
    @DisplayName("반품 정책 활성화 - 성공")
    void activateRefundPolicy_성공() throws Exception {
        // given
        doNothing().when(refundPolicyService).activate(anyLong());

        // when, then
        mockMvc.perform(patch("/refundPolicies/activate/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("환불 정책 활성화 - 실패")
    void activateRefundPolicy_실패() throws Exception {
        // given
        doThrow(new RefundPolicyNotFoundException(1L)).when(refundPolicyService).activate(anyLong());

        // when, then
        mockMvc.perform(patch("/refundPolicies/activate/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 비활성화 - 성공")
    void deactivateRefundPolicy_성공() throws Exception {
        // given
        doNothing().when(refundPolicyService).deactivate(anyLong());

        // when, then
        mockMvc.perform(patch("/refundPolicies/deactivate/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 비활성화 - 실패")
    void deactivateRefundPolicy_실패() throws Exception {
        // given
        doThrow(new RefundPolicyNotFoundException(1L)).when(refundPolicyService).deactivate(anyLong());

        // when, then
        mockMvc.perform(patch("/refundPolicies/deactivate/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 삭제 - 성공")
    void deleteRefundPolicy_성공() throws Exception {
        // given
        doNothing().when(refundPolicyService).deleteRefundPolicy(anyLong());

        // when, then
        mockMvc.perform(delete("/refundPolicies/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("반품 정책 삭제 - 실패")
    void deleteRefundPolicy_실패() throws Exception {
        // given
        doThrow(new RefundPolicyNotFoundException(1L)).when(refundPolicyService).deleteRefundPolicy(anyLong());

        // when, then
        mockMvc.perform(delete("/refundPolicies/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
