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
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.order.orderBook.entity.OrderBook;
import shop.ink3.api.review.dto.ReviewRequest;
import shop.ink3.api.review.dto.ReviewResponse;
import shop.ink3.api.review.service.ReviewService;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
            .ISBN("1234567890123")
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

        reviewRequest = new ReviewRequest(user, orderBook1, "title1", "content1", 5);
        reviewResponse = new ReviewResponse(1L, user, orderBook1, "title1", "content1", 5, LocalDateTime.now());
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
            1L, user, orderBook1, "title1", "content1", 5, LocalDateTime.now()
        );

        when(reviewService.getReviewByUserId(user.getId())).thenReturn(response);

        mockMvc.perform(get("/reviews/user/1", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("title1"))
            .andExpect(jsonPath("$.content").value("content1"))
            .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("한 도서의 리뷰 전체 조회")
    void getReviewsByBookId() throws Exception {
        List<ReviewResponse> content = List.of(
            new ReviewResponse(1L, user, orderBook1, "title1", "content1", 5, LocalDateTime.now()),
            new ReviewResponse(2L, user, orderBook2, "title2", "content2", 4, LocalDateTime.now())
        );

        Page<ReviewResponse> reviewResponses = new PageImpl<>(
            content,
            PageRequest.of(0, 10),
            content.size()
        );

        Mockito.when(reviewService.getReviewsByBookId(any(), eq(book.getId()))).thenReturn(reviewResponses);

        mockMvc.perform(get("/reviews/book/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/reviews/1"))
            .andExpect(status().isNoContent());
    }
}
