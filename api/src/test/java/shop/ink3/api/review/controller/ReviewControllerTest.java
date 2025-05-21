package shop.ink3.api.review.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.common.dto.PageResponse;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.dto.ReviewUpdateRequest;
import shop.ink3.api.review.service.ReviewService;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Publisher publisher;
    private Book book;
    private OrderBook orderBook1;
    private OrderBook orderBook2;
    private ReviewRequest reviewRequest;
    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .loginId("test")
            .name("test")
            .email("test@test.com")
            .phone("010-1234-5678")
            .birthday(LocalDate.of(2025, 1, 1))
            .point(1000)
            .status(UserStatus.ACTIVE)
            .lastLoginAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        publisher = Publisher.builder()
            .id(1L)
            .name("출판사1")
            .build();

        book = Book.builder()
            .id(1L)
            .isbn("1234567890123")
            .title("예제 책 제목")
            .contents("책 내용 요약")
            .description("책 상세 설명")
            .publishedAt(LocalDate.of(2024, 1, 1))
            .originalPrice(20000)
            .salePrice(18000)
            .discountRate((18000 * 100) / 20000)
            .quantity(100)
            .status(BookStatus.AVAILABLE)
            .isPackable(true)
            .thumbnailUrl("https://example.com/image.jpg")
            .publisher(publisher)
            .build();

        orderBook1 = OrderBook.builder()
            .id(1L)
            .order(null)
            .book(book)
            .packaging(null)
            .couponStore(null)
            .price(20000)
            .quantity(100)
            .build();

        orderBook2 = OrderBook.builder()
            .id(2L)
            .order(null)
            .book(book)
            .packaging(null)
            .couponStore(null)
            .price(20000)
            .quantity(100)
            .build();

        reviewRequest = new ReviewRequest(user.getId(), orderBook1.getId(), "title1", "content1", 5);
        reviewResponse = new ReviewResponse(1L, user.getId(), orderBook1.getId(), "title1", "content1", 5, LocalDateTime.now());
    }

    @Test
    @DisplayName("리뷰 등록")
    void addReview() throws Exception {
        Mockito.when(reviewService.addReview(any(ReviewRequest.class))).thenReturn(reviewResponse);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("title1"))
            .andExpect(jsonPath("$.content").value("content1"));
    }

    @Test
    @DisplayName("주문 도서의 리뷰 조회")
    void getReviewByUserId() throws Exception {
        ReviewResponse response = new ReviewResponse(
            1L, user.getId(), orderBook1.getId(), "title1", "content1", 5, LocalDateTime.now()
        );

        when(reviewService.getReviewByUserId(user.getId())).thenReturn(response);

        mockMvc.perform(get("/reviews/user/{userId}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("title1"))
            .andExpect(jsonPath("$.content").value("content1"))
            .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("도서의 리뷰 목록 조회")
    void getReviewsByBookId() throws Exception {
        List<ReviewResponse> reviewList = List.of(
            new ReviewResponse(1L, user.getId(), orderBook1.getId(), "title1", "content1", 5, LocalDateTime.now()),
            new ReviewResponse(2L, user.getId(), orderBook2.getId(), "title2", "content2", 4, LocalDateTime.now())
        );

        PageResponse<ReviewResponse> pageResponse = new PageResponse<>(
            reviewList,
            0,
            10,
            2L,
            1,
            false,
            false
        );

        when(reviewService.getReviewsByBookId(any(), eq(book.getId()))).thenReturn(pageResponse);

        mockMvc.perform(get("/reviews/book/{bookId}", book.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.content[0].title").value("title1"))
            .andExpect(jsonPath("$.content[1].title").value("title2"));
    }

    @Test
    @DisplayName("리뷰 수정")
    void updateReview() throws Exception {
        ReviewUpdateRequest request = new ReviewUpdateRequest("updatedTitle", "updatedContent", 4);
        ReviewResponse response = new ReviewResponse(1L, user.getId(), orderBook1.getId(), "updatedTitle", "updatedContent", 4, LocalDateTime.now());

        when(reviewService.updateReview(eq(1L), any(ReviewUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/reviews/{reviewId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("updatedTitle"))
            .andExpect(jsonPath("$.content").value("updatedContent"))
            .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/reviews/1"))
            .andExpect(status().isNoContent());
    }
}
