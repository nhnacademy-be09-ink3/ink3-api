package shop.ink3.api.order.cart.controller;

import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.order.cart.dto.CartRequest;
import shop.ink3.api.order.cart.dto.CartResponse;
import shop.ink3.api.order.cart.service.CartService;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;

@WebMvcTest(CartController.class)
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Book book1;
    private Book book2;
    private Publisher publisher;
    private CartRequest cartRequest;
    private CartResponse cartResponse;

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

        book1 = Book.builder()
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

        book2 = Book.builder()
            .id(2L)
            .ISBN("1234567890124")
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

        cartRequest = new CartRequest(user.getId(), book1.getId(), 100);
        cartResponse = new CartResponse(1L, user.getId(), book1.getId(), 100);
    }

    @Test
    @DisplayName("장바구니에 도서 추가")
    void addCart() throws Exception {
        Mockito.when(cartService.addCartItem(any(CartRequest.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    @DisplayName("장바구니 목록 조회")
    void getCarts() throws Exception {
        List<CartResponse> cartResponses = List.of(
            new CartResponse(1L, user.getId(), book1.getId(), 100),
            new CartResponse(2L, user.getId(), book2.getId(), 100)
        );

        Mockito.when(cartService.getCartItemsByUserId(user.getId())).thenReturn(cartResponses);

        mockMvc.perform(get("/carts/user/1"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 특정 도서 삭제")
    void deleteCart() throws Exception {
        mockMvc.perform(delete("/carts/1"))
            .andExpect(status().isNoContent());
    }
}
