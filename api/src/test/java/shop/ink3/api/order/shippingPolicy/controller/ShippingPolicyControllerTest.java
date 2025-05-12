package shop.ink3.api.order.shippingPolicy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
import java.time.LocalDateTime;
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
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyCreateRequest;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyResponse;
import shop.ink3.api.order.shippingPolicy.dto.ShippingPolicyUpdateRequest;
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
        // given
        ShippingPolicy policy = ShippingPolicy.builder().id(1L).build();
        ShippingPolicyResponse shippingPolicyResponse = ShippingPolicyResponse.from(policy);
        when(shippingPolicyService.getShippingPolicy(1L)).thenReturn(shippingPolicyResponse);

        // when then
        mockMvc.perform(get("/shippingPolicies/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
        verify(shippingPolicyService).getShippingPolicy(anyLong());
    }

    @Test
    @DisplayName("특정 배송 정책 ID 찾기 - 실패")
    void getShippingPolicy_실패() throws Exception {
        // given
        when(shippingPolicyService.getShippingPolicy(1L)).thenThrow(new ShippingPolicyNotFoundException(1L));

        // when, then
        mockMvc.perform(get("/shippingPolicies/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }


    @Test
    @DisplayName("활성화된 배송 정책 조회 - 성공")
    void getActivateShippingPolicy_성공() throws Exception {
        // given
        ShippingPolicy policy = ShippingPolicy.builder().id(1L).build();
        ShippingPolicyResponse shippingPolicyResponse = ShippingPolicyResponse.from(policy);
        when(shippingPolicyService.getActivateShippingPolicy()).thenReturn(shippingPolicyResponse);

        // when then
        mockMvc.perform(get("/shippingPolicies/activate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
        verify(shippingPolicyService).getActivateShippingPolicy();
    }

    @Test
    @DisplayName("활성화된 배송 정책 조회 - 실패")
    void getActivateShippingPolicy_실패() throws Exception {
        // given
        when(shippingPolicyService.getActivateShippingPolicy()).thenThrow(new ShippingPolicyNotFoundException());

        // when then
        mockMvc.perform(get("/shippingPolicies/activate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }


    @Test
    @DisplayName("배송 정책 목록 조회 - 성공")
    void getShippingPolicies_성공() throws Exception {
        // given
        PageResponse<ShippingPolicyResponse> response = new PageResponse<>(
                List.of(
                        ShippingPolicyResponse.from(ShippingPolicy.builder().id(1L).name("테스트1").fee(2000).build()),
                        ShippingPolicyResponse.from(ShippingPolicy.builder().id(2L).name("테스트2").fee(3000).build())
                ),
                0, 2, 2L, 1, false, false
        );
        when(shippingPolicyService.getShippingPolicyList(any())).thenReturn(response);

        // when, then
        mockMvc.perform(get("/shippingPolicies")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("테스트1"))
                .andExpect(jsonPath("$.data.content[1].id").value(2))
                .andExpect(jsonPath("$.data.content[1].name").value("테스트2"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false));
        verify(shippingPolicyService).getShippingPolicyList(any());
    }

    @Test
    @DisplayName("배송 정책 목록 조회 - 실패")
    void getShippingPolicies_실패() throws Exception {
        // given
        when(shippingPolicyService.getShippingPolicyList(any())).thenThrow(new ShippingPolicyNotFoundException());

        // when, then
        mockMvc.perform(get("/shippingPolicies")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("배송 정책 생성 - 성공")
    void createShippingPolicy() throws Exception {
        // given
        ShippingPolicyCreateRequest request = new ShippingPolicyCreateRequest(
                "테스트1",
                30000,
                5500
        );
        ShippingPolicy shippingPolicy = ShippingPolicy.builder()
                .id(1L)
                .name(request.getName())
                .threshold(request.getThreshold())
                .fee(request.getFee())
                .isAvailable(false)
                .createdAt(LocalDateTime.now())
                .build();
        ShippingPolicyResponse response = ShippingPolicyResponse.from(shippingPolicy);
        when(shippingPolicyService.createShippingPolicy(any())).thenReturn(response);

        // when, then
        mockMvc.perform(post("/shippingPolicies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
        verify(shippingPolicyService).createShippingPolicy(any());
    }

    @Test
    @DisplayName("배송 정책 생성 - 실패")
    void createShippingPolicy_실패() throws Exception {
        // given
        ShippingPolicyCreateRequest request = new ShippingPolicyCreateRequest("테스트1", 30000, 5500);
        when(shippingPolicyService.createShippingPolicy(any()))
                .thenThrow(new ShippingPolicyNotFoundException(1L)); // 예외 발생 가정

        // when, then
        mockMvc.perform(post("/shippingPolicies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("배송 정책 수정 - 성공")
    void updateShippingPolicy() throws Exception {
        // given
        ShippingPolicyUpdateRequest request = new ShippingPolicyUpdateRequest(
                "테스트1",
                30000,
                5500
        );
        ShippingPolicy shippingPolicy = ShippingPolicy.builder()
                .id(1L)
                .name(request.getName())
                .threshold(request.getThreshold())
                .fee(request.getFee())
                .isAvailable(false)
                .createdAt(LocalDateTime.now())
                .build();
        ShippingPolicyResponse response = ShippingPolicyResponse.from(shippingPolicy);
        when(shippingPolicyService.updateShippingPolicy(anyLong(), any())).thenReturn(response);


        // when, then
        mockMvc.perform(put("/shippingPolicies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
        verify(shippingPolicyService).updateShippingPolicy(anyLong(), any());
    }

    @Test
    @DisplayName("배송 정책 수정 - 실패")
    void updateShippingPolicy_실패() throws Exception {
        // given
        ShippingPolicyUpdateRequest request = new ShippingPolicyUpdateRequest("테스트1", 30000, 5500);
        when(shippingPolicyService.updateShippingPolicy(anyLong(), any()))
                .thenThrow(new ShippingPolicyNotFoundException(1L)); // 예외 발생 가정

        // when, then
        mockMvc.perform(put("/shippingPolicies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("배송 정책 활성화 - 성공")
    void activateShippingPolicy_성공() throws Exception {
        // given
        doNothing().when(shippingPolicyService).activate(anyLong());

        // when, then
        mockMvc.perform(patch("/shippingPolicies/activate/1"))
                .andExpect(status().isOk())
                .andDo(print());
        verify(shippingPolicyService).activate(anyLong());
    }

    @Test
    @DisplayName("배송 정책 활성화 - 실패")
    void activateShippingPolicy_실패() throws Exception {
        // given
        doThrow(new ShippingPolicyNotFoundException(1L)).when(shippingPolicyService).activate(anyLong());

        // when, then
        mockMvc.perform(patch("/shippingPolicies/activate/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("배송 정책 비활성화 - 성공")
    void deactivateShippingPolicy_성공() throws Exception {
        // given
        doNothing().when(shippingPolicyService).deactivate(anyLong());

        // when, then
        mockMvc.perform(patch("/shippingPolicies/deactivate/1"))
                .andExpect(status().isOk())
                .andDo(print());
        verify(shippingPolicyService).deactivate(anyLong());
    }

    @Test
    @DisplayName("배송 정책 비활성화 - 실패")
    void deactivateShippingPolicy_실패() throws Exception {
        // given
        doThrow(new ShippingPolicyNotFoundException(1L)).when(shippingPolicyService).deactivate(anyLong());

        // when, then
        mockMvc.perform(patch("/shippingPolicies/deactivate/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("배송 정책 삭제 - 성공")
    void deleteShippingPolicy_성공() throws Exception {
        // given
        doNothing().when(shippingPolicyService).deleteShippingPolicy(anyLong());

        // when, then
        mockMvc.perform(delete("/shippingPolicies/1"))
                .andExpect(status().isOk())
                .andDo(print());
        verify(shippingPolicyService).deleteShippingPolicy(anyLong());
    }

    @Test
    @DisplayName("배송 정책 삭제 - 실패")
    void deleteShippingPolicy_실패() throws Exception {
        // given
        doThrow(new ShippingPolicyNotFoundException(1L)).when(shippingPolicyService).deleteShippingPolicy(anyLong());

        // when, then
        mockMvc.perform(delete("/shippingPolicies/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
