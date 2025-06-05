package shop.ink3.api.user.point.history.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.point.history.dto.PointHistoryResponse;
import shop.ink3.api.user.point.history.entity.PointHistory;
import shop.ink3.api.user.point.history.service.PointService;

@WebMvcTest(shop.ink3.api.user.point.history.controller.MePointController.class)
class MePointControllerTest {
    @MockitoBean
    PointService pointService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getCurrentUserPointHistories() throws Exception {
        PageResponse<PointHistoryResponse> response = new PageResponse<>(
                List.of(
                        PointHistoryResponse.from(PointHistory.builder().id(1L).build()),
                        PointHistoryResponse.from(PointHistory.builder().id(2L).build())
                ),
                0, 2, 2L, 1, false, false
        );
        when(pointService.getPointHistoriesByUserId(anyLong(), any())).thenReturn(response);
        mockMvc.perform(get("/users/me/points")
                        .header("X-User-Id", 1)
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
}
