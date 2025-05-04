package shop.ink3.api.user.membership.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.user.membership.dto.MembershipCreateRequest;
import shop.ink3.api.user.membership.dto.MembershipResponse;
import shop.ink3.api.user.membership.dto.MembershipUpdateRequest;
import shop.ink3.api.user.membership.entity.Membership;
import shop.ink3.api.user.membership.exception.MembershipNotFoundException;
import shop.ink3.api.user.membership.service.MembershipService;

@WebMvcTest(MembershipController.class)
class MembershipControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MembershipService membershipService;

    @Test
    void getMembership() throws Exception {
        Membership membership = Membership.builder()
                .id(1L)
                .name("test")
                .conditionAmount(1)
                .pointRate(1)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        MembershipResponse response = MembershipResponse.from(membership);
        when(membershipService.getMembership(1L)).thenReturn(response);
        mockMvc.perform(get("/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.conditionAmount").value(1))
                .andExpect(jsonPath("$.data.pointRate").value(1))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andDo(print());
    }

    @Test
    void getMembershipWithNotFound() throws Exception {
        when(membershipService.getMembership(1L)).thenThrow(new MembershipNotFoundException(1L));
        mockMvc.perform(get("/memberships/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void getMemberships() throws Exception {
        PageResponse<MembershipResponse> pageResponse = new PageResponse<>(
                List.of(
                        new MembershipResponse(1L, "test1", 1, 1, true, LocalDateTime.now()),
                        new MembershipResponse(2L, "test2", 2, 2, false, LocalDateTime.now())
                ),
                0, 2, 2L, 1, false, false
        );
        when(membershipService.getMemberships(any())).thenReturn(pageResponse);
        mockMvc.perform(get("/memberships")
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
    void createMembership() throws Exception {
        MembershipCreateRequest request = new MembershipCreateRequest("test", 1, 1);
        Membership membership = Membership.builder()
                .id(1L)
                .name("test")
                .conditionAmount(1)
                .pointRate(1)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .build();
        MembershipResponse response = MembershipResponse.from(membership);
        when(membershipService.createMembership(request)).thenReturn(response);
        mockMvc.perform(post("/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.conditionAmount").value(1))
                .andExpect(jsonPath("$.data.pointRate").value(1))
                .andExpect(jsonPath("$.data.isActive").value(false))
                .andDo(print());
    }

    @Test
    void updateMembership() throws Exception {
        MembershipUpdateRequest request = new MembershipUpdateRequest("test", 1, 1);
        Membership membership = Membership.builder()
                .id(1L)
                .name("test")
                .conditionAmount(1)
                .pointRate(1)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .build();
        MembershipResponse response = MembershipResponse.from(membership);
        when(membershipService.updateMembership(1L, request)).thenReturn(response);
        mockMvc.perform(put("/memberships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.conditionAmount").value(1))
                .andExpect(jsonPath("$.data.pointRate").value(1))
                .andExpect(jsonPath("$.data.isActive").value(false))
                .andDo(print());
    }

    @Test
    void updateMembershipWithNotFound() throws Exception {
        MembershipUpdateRequest request = new MembershipUpdateRequest("test", 1, 1);
        when(membershipService.updateMembership(1L, request)).thenThrow(new MembershipNotFoundException(1L));
        mockMvc.perform(put("/memberships/1")
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
    void activateMembership() throws Exception {
        doNothing().when(membershipService).activateMembership(1L);
        mockMvc.perform(patch("/memberships/1/activate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void activateMembershipWithNotFound() throws Exception {
        doThrow(new MembershipNotFoundException(1L)).when(membershipService).activateMembership(1L);
        mockMvc.perform(patch("/memberships/1/activate"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deactivateMembership() throws Exception {
        mockMvc.perform(patch("/memberships/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deactivateMembershipWithNotFound() throws Exception {
        doThrow(new MembershipNotFoundException(1L)).when(membershipService).deactivateMembership(1L);
        mockMvc.perform(patch("/memberships/1/deactivate"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deleteMembership() throws Exception {
        doNothing().when(membershipService).deleteMembership(1L);
        mockMvc.perform(delete("/memberships/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }

    @Test
    void deleteMembershipWithNotFound() throws Exception {
        doThrow(new MembershipNotFoundException(1L)).when(membershipService).deleteMembership(1L);
        mockMvc.perform(delete("/memberships/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andDo(print());
    }
}
