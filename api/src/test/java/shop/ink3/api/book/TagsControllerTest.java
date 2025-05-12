package shop.ink3.api.books.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.books.dto.TagCreateRequest;
import shop.ink3.api.books.dto.TagResponse;
import shop.ink3.api.books.dto.TagUpdateRequest;
import shop.ink3.api.books.service.TagsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagsController.class)
@AutoConfigureMockMvc
class TagsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagsService tagsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTag() throws Exception {
        TagCreateRequest request = new TagCreateRequest("java");
        TagResponse response = new TagResponse(1L, "java");

        given(tagsService.createTag(any())).willReturn(response);

        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("java"));
    }

    @Test
    void getAllTags() throws Exception {
        List<TagResponse> tags = List.of(
                new TagResponse(1L, "java"),
                new TagResponse(2L, "spring")
        );
        given(tagsService.getTags()).willReturn(tags);

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("java"))
                .andExpect(jsonPath("$.data[1].name").value("spring"));
    }

    @Test
    void updateTag() throws Exception {
        TagUpdateRequest request = new TagUpdateRequest("springboot");
        TagResponse response = new TagResponse(1L, "springboot");

        given(tagsService.updateTag(eq(1L), any())).willReturn(response);

        mockMvc.perform(put("/tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("springboot"));
    }

    @Test
    void deleteTag() throws Exception {
        willDoNothing().given(tagsService).deleteTag(1L);

        mockMvc.perform(delete("/tags/1"))
                .andExpect(status().isNoContent());
    }
}