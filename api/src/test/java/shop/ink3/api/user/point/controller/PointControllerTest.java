package shop.ink3.api.user.point.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import shop.ink3.api.user.point.dto.PointHistoryCreateRequest;
import shop.ink3.api.user.point.dto.PointHistoryResponse;
import shop.ink3.api.user.point.dto.PointHistoryUpdateRequest;
import shop.ink3.api.user.point.entity.PointHistory;
import shop.ink3.api.user.point.entity.PointHistoryStatus;
import shop.ink3.api.user.point.exception.PointHistoryNotFoundException;
import shop.ink3.api.user.point.service.PointService;
import shop.ink3.api.user.user.dto.UserPointRequest;
import shop.ink3.api.user.user.exception.UserNotFoundException;

@WebMvcTest(PointController.class)
class PointControllerTest {
    @MockitoBean
    PointService pointService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getPointHistory() throws Exception {
        PointHistory pointHistory = PointHistory.builder()
                .id(1L)
                .build();
        PointHistoryResponse response = PointHistoryResponse.from(pointHistory);
        when(pointService.getPointHistory(1L, 1L)).thenReturn(response);
        mockMvc.perform(get("/users/1/points/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(print());
    }

    @Test
    void getPointHistoryNotFound() throws Exception {
        when(pointService.getPointHistory(1L, 1L)).thenThrow(new PointHistoryNotFoundException(1L));
        mockMvc.perform(get("/users/1/points/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getPointHistories() throws Exception {
        PageResponse<PointHistoryResponse> response = new PageResponse<>(
                List.of(
                        PointHistoryResponse.from(PointHistory.builder().id(1L).build()),
                        PointHistoryResponse.from(PointHistory.builder().id(2L).build())
                ),
                0, 2, 2L, 1, false, false
        );
        when(pointService.getPointHistoriesByUserId(anyLong(), any())).thenReturn(response);
        mockMvc.perform(get("/users/1/points")
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
                .andDo(print());
    }

    @Test
    void createPointHistory() throws Exception {
        PointHistoryCreateRequest request = new PointHistoryCreateRequest(1, PointHistoryStatus.EARN, "test");
        PointHistory pointHistory = PointHistory.builder().id(1L).build();
        PointHistoryResponse response = PointHistoryResponse.from(pointHistory);
        when(pointService.createPointHistory(1L, request)).thenReturn(response);
        mockMvc.perform(post("/users/1/points")
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
    void createPointHistoryUserNotFound() throws Exception {
        PointHistoryCreateRequest request = new PointHistoryCreateRequest(1, PointHistoryStatus.EARN, "test");
        when(pointService.createPointHistory(1L, request)).thenThrow(new UserNotFoundException(1L));
        mockMvc.perform(post("/users/1/points")
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
    void updatePointHistory() throws Exception {
        PointHistoryUpdateRequest request = new PointHistoryUpdateRequest(2, PointHistoryStatus.CANCEL, "new");
        PointHistoryResponse response = PointHistoryResponse.from(PointHistory.builder().id(1L).build());
        when(pointService.updatePointHistory(1L, 1L, request)).thenReturn(response);
        mockMvc.perform(put("/users/1/points/1")
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
    void updatePointHistoryNotFound() throws Exception {
        PointHistoryUpdateRequest request = new PointHistoryUpdateRequest(2, PointHistoryStatus.CANCEL, "new");
        when(pointService.updatePointHistory(1L, 1L, request)).thenThrow(new PointHistoryNotFoundException(1L));
        mockMvc.perform(put("/users/1/points/1")
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
    void deletePointHistory() throws Exception {
        doNothing().when(pointService).deletePointHistory(1L, 1L);
        mockMvc.perform(delete("/users/1/points/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void deletePointHistoryWithNotFound() throws Exception {
        doThrow(new PointHistoryNotFoundException(1L)).when(pointService).deletePointHistory(1L, 1L);
        mockMvc.perform(delete("/users/1/points/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void earnPoints() throws Exception {
        UserPointRequest request = new UserPointRequest(1000);
        doNothing().when(pointService).earnPoint(1L, request);
        mockMvc.perform(post("/users/1/points/earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void earnPointsWithNotFound() throws Exception {
        UserPointRequest request = new UserPointRequest(1000);
        doThrow(new UserNotFoundException(1L)).when(pointService).earnPoint(1L, request);
        mockMvc.perform(post("/users/1/points/earn")
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
    void usePoints() throws Exception {
        UserPointRequest request = new UserPointRequest(1000);
        doNothing().when(pointService).usePoint(1L, request);
        mockMvc.perform(post("/users/1/points/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    void usePointsWithNotFound() throws Exception {
        UserPointRequest request = new UserPointRequest(1000);
        doThrow(new UserNotFoundException(1L)).when(pointService).usePoint(1L, request);
        mockMvc.perform(post("/users/1/points/use")
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
}
