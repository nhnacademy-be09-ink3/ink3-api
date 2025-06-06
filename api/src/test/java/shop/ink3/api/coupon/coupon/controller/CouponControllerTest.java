package shop.ink3.api.coupon.coupon.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.coupon.coupon.dto.CouponCreateRequest;
import shop.ink3.api.coupon.coupon.dto.CouponResponse;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.BookInfo;
import shop.ink3.api.coupon.coupon.dto.CouponResponse.CategoryInfo;
import shop.ink3.api.coupon.coupon.dto.CouponUpdateRequest;
import shop.ink3.api.coupon.coupon.service.Impl.CouponServiceImpl;

@WebMvcTest()
@ContextConfiguration(classes = CouponController.class)
public class CouponControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponServiceImpl couponService;

    private static final LocalDateTime now = LocalDateTime.of(2025, 5, 31, 0, 0);
    private static final LocalDateTime expires = now.plusDays(5);

    @Test
    void create() throws Exception {

        CouponCreateRequest couponCreateRequest = new CouponCreateRequest(
                1L,
                "test",
                now,
                expires,
                now,
                Collections.emptyList(),
                Collections.emptyList()
        );

        CouponResponse couponResponse = new CouponResponse(
                1L,
                1L,
                "test",
                now,
                expires,
                now,
                Collections.emptyList(),
                Collections.emptyList()
        );

        when(couponService.createCoupon(any(CouponCreateRequest.class)))
                .thenReturn(couponResponse);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.data.couponId").value(1))
                .andExpect(jsonPath("$.data.policyId").value(1))
                .andExpect(jsonPath("$.data.name").value("test"))
                .andExpect(jsonPath("$.data.issuableFrom").value(now.toString() + ":00"))
                .andExpect(jsonPath("$.data.expiresAt").value(expires.toString() + ":00"))
                .andExpect(jsonPath("$.data.createdAt").value(now.toString() + ":00"))
                .andExpect(jsonPath("$.data.books").isArray())
                .andExpect(jsonPath("$.data.books").isEmpty())
                .andExpect(jsonPath("$.data.categories").isArray())
                .andExpect(jsonPath("$.data.categories").isEmpty());

        verify(couponService).createCoupon(any(CouponCreateRequest.class));
    }

    @Test
    @DisplayName("POST /coupons - 도서/카테고리 포함해서 생성")
    void createBooksAndCategories() throws Exception {
        LocalDateTime now = LocalDateTime.of(2025, 5, 31, 0, 0);
        LocalDateTime expires = now.plusDays(5);

        CouponCreateRequest requestDto = new CouponCreateRequest(
                1L,
                "test-coupon",
                now,
                expires,
                now,
                List.of(100L),   // bookIdList 에 100L 하나
                List.of(200L)    // categoryIdList 에 200L 하나
        );

        BookInfo bookInfo = new BookInfo(11L, 100L, "Java Programming");
        CategoryInfo categoryInfo = new CategoryInfo(22L, 200L, "Fiction");

        CouponResponse mockedResponse = new CouponResponse(
                1L,
                1L,
                "test-coupon",
                now,
                expires,
                now,
                List.of(bookInfo),       // books 리스트
                List.of(categoryInfo)    // categories 리스트
        );

        when(couponService.createCoupon(any(CouponCreateRequest.class)))
                .thenReturn(mockedResponse);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // CommonResponse.status 필드가 생성 성공 코드(201)인지 확인
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))

                // data 안에 couponId, policyId, name 확인
                .andExpect(jsonPath("$.data.couponId").value(1))
                .andExpect(jsonPath("$.data.policyId").value(1))
                .andExpect(jsonPath("$.data.name").value("test-coupon"))

                // data.books[0] 에 있는 originId, id, title 검증
                .andExpect(jsonPath("$.data.books[0].originId").value(11))
                .andExpect(jsonPath("$.data.books[0].id").value(100))
                .andExpect(jsonPath("$.data.books[0].title").value("Java Programming"))

                // data.categories[0] 에 있는 originId, id, name 검증
                .andExpect(jsonPath("$.data.categories[0].originId").value(22))
                .andExpect(jsonPath("$.data.categories[0].id").value(200))
                .andExpect(jsonPath("$.data.categories[0].name").value("Fiction"));

        verify(couponService).createCoupon(any(CouponCreateRequest.class));
    }

    @Test
    @DisplayName("GET /coupons/{id} - 단건 조회 성공")
    void getById_success() throws Exception {
        Long couponId = 1L;
        CouponResponse resp = new CouponResponse(
                couponId,
                10L,
                "sample-coupon",
                now,
                expires,
                now,
                Collections.emptyList(),
                Collections.emptyList()
        );
        when(couponService.getCouponById(couponId)).thenReturn(resp);

        mockMvc.perform(get("/coupons/{couponId}", couponId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.couponId").value(1))
                .andExpect(jsonPath("$.data.policyId").value(10))
                .andExpect(jsonPath("$.data.name").value("sample-coupon"))
                .andExpect(jsonPath("$.data.issuableFrom").value(now.toString() + ":00"))
                .andExpect(jsonPath("$.data.expiresAt").value(expires.toString() + ":00"))
                .andExpect(jsonPath("$.data.books").isArray())
                .andExpect(jsonPath("$.data.books").isEmpty())
                .andExpect(jsonPath("$.data.categories").isArray())
                .andExpect(jsonPath("$.data.categories").isEmpty());

        verify(couponService).getCouponById(couponId);
    }

    @Test
    @DisplayName("GET /coupons - 전체 조회 성공 (Pageable 고려, content 필드 확인)")
    void getAll_success_withPageable() throws Exception {
        // given
        CouponResponse c1 = new CouponResponse(
                1L, 10L, "coup1", now, expires, now,
                Collections.emptyList(), Collections.emptyList()
        );
        CouponResponse c2 = new CouponResponse(
                2L, 20L, "coup2", now, expires, now,
                Collections.emptyList(), Collections.emptyList()
        );

        PageRequest pageable = PageRequest.of(0, 2);
        PageResponse<CouponResponse> pageResponse = PageResponse.from(
                new PageImpl<>(List.of(c1, c2), pageable, 2)
        );

        when(couponService.getAllCoupons(eq(pageable)))
                .thenReturn(pageResponse);

        // when / then
        mockMvc.perform(get("/coupons")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                // $.data.content 배열 검증
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].couponId").value(1))
                .andExpect(jsonPath("$.data.content[1].couponId").value(2));

        verify(couponService).getAllCoupons(eq(pageable));
    }


    @Test
    @DisplayName("PUT /coupons/{id} - 수정 성공")
    void updateCoupon_success() throws Exception {
        Long couponId = 5L;
        List<Long> bookIds = List.of(100L);
        List<Long> categoryIds = List.of(200L);

        CouponUpdateRequest updateReq = new CouponUpdateRequest(
                30L,
                "updated-name",
                now,
                expires.plusDays(2),
                now,
                bookIds,
                categoryIds
        );

        BookInfo bookInfo = new BookInfo(11L, 100L, "BookTitle");
        CategoryInfo catInfo = new CategoryInfo(22L, 200L, "CatName");
        CouponResponse updatedResp = new CouponResponse(
                couponId,
                30L,
                "updated-name",
                now,
                expires.plusDays(2),
                now,
                List.of(bookInfo),
                List.of(catInfo)
        );

        when(couponService.updateCoupon(eq(couponId), any(CouponUpdateRequest.class)))
                .thenReturn(updatedResp);

        mockMvc.perform(put("/coupons/{couponId}", couponId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.couponId").value(5))
                .andExpect(jsonPath("$.data.policyId").value(30))
                .andExpect(jsonPath("$.data.name").value("updated-name"))
                .andExpect(jsonPath("$.data.books[0].id").value(100))
                .andExpect(jsonPath("$.data.categories[0].id").value(200));

        verify(couponService).updateCoupon(eq(couponId), any(CouponUpdateRequest.class));
    }

    @Test
    @DisplayName("DELETE /coupons/{id} - 삭제 성공")
    void deleteById_success() throws Exception {
        Long couponId = 7L;

        mockMvc.perform(delete("/coupons/{couponId}", couponId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(couponService).deleteCouponById(couponId);
    }

    @Test
    @DisplayName("GET /coupons/by-book/{bookId} - 도서별 조회 성공 (Pageable 고려, content 필드 확인)")
    void getByBookId_success_withPageable() throws Exception {
        Long bookId = 100L;
        BookInfo bookInfo = new BookInfo(11L, 100L, "BookTitle");
        CouponResponse resp = new CouponResponse(
                9L, 40L, "from-book", now, expires, now,
                List.of(bookInfo), Collections.emptyList()
        );

        // Pageable 파라미터 (예: page=0, size=2)
        PageRequest pageable = PageRequest.of(0, 2);
        PageResponse<CouponResponse> pageResponse = PageResponse.from(
                new PageImpl<>(List.of(resp), pageable, 1)
        );

        // 서비스가 getCouponsByBookId(bookId, pageable)를 호출하면 pageResponse를 반환하도록 모킹
        when(couponService.getCouponsByBookId(eq(bookId), eq(pageable)))
                .thenReturn(pageResponse);

        // MockMvc로 GET 요청 시 page, size 파라미터를 함께 보냄
        mockMvc.perform(get("/coupons/by-book/{bookId}", bookId)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // CommonResponse.status가 200인지 확인
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                // 실제 데이터 리스트는 $.data.content 배열 안에 들어있음
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].couponId").value(9))
                .andExpect(jsonPath("$.data.content[0].books[0].id").value(100));

        // 서비스 메서드가 정확히 (bookId, pageable) 인자로 호출되었는지 검증
        verify(couponService).getCouponsByBookId(eq(bookId), eq(pageable));
    }


    @Test
    @DisplayName("GET /coupons/by-category/{categoryId} - 카테고리별 조회 성공 (Pageable 고려, content 필드 확인)")
    void getByCategoryId_success_withPageable() throws Exception {
        Long categoryId = 200L;
        CategoryInfo catInfo = new CategoryInfo(22L, 200L, "CatName");
        CouponResponse resp = new CouponResponse(
                15L, 50L, "from-cat", now, expires, now,
                Collections.emptyList(), List.of(catInfo)
        );

        // Pageable 파라미터 (예: page=0, size=2)
        PageRequest pageable = PageRequest.of(0, 2);
        PageResponse<CouponResponse> pageResponse = PageResponse.from(
                new PageImpl<>(List.of(resp), pageable, 1)
        );

        // 서비스가 getCouponsByCategoryId(categoryId, pageable)를 호출하면 pageResponse를 반환하도록 모킹
        when(couponService.getCouponsByCategoryId(eq(categoryId), eq(pageable)))
                .thenReturn(pageResponse);

        // MockMvc로 GET 요청 시 page, size 파라미터를 함께 보냄
        mockMvc.perform(get("/coupons/by-category/{categoryId}", categoryId)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // CommonResponse.status가 200인지 확인
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                // 실제 데이터 리스트는 $.data.content 배열 안에 들어있음
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].couponId").value(15))
                .andExpect(jsonPath("$.data.content[0].categories[0].id").value(200));

        // 서비스 메서드가 정확히 (categoryId, pageable) 인자로 호출되었는지 검증
        verify(couponService).getCouponsByCategoryId(eq(categoryId), eq(pageable));
    }

}
