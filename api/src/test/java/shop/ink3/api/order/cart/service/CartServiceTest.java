package shop.ink3.api.order.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.entity.BookStatus;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.common.exception.BookNotFoundException;
import shop.ink3.api.book.publisher.entity.Publisher;
import shop.ink3.api.order.cart.dto.CartRequest;
import shop.ink3.api.order.cart.dto.CartResponse;
import shop.ink3.api.order.cart.dto.CartUpdateRequest;
import shop.ink3.api.order.cart.dto.GuestCartRequest;
import shop.ink3.api.order.cart.entity.Cart;
import shop.ink3.api.order.cart.repository.CartRepository;
import shop.ink3.api.order.common.exception.CartNotFoundException;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.entity.UserStatus;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

class CartServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, String, CartResponse> hashOperations;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Book book1;
    private Book book2;
    private Publisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForHash()).thenReturn((HashOperations) hashOperations);

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

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book1.getId())).thenReturn(Optional.of(book1));
        when(bookRepository.findById(book2.getId())).thenReturn(Optional.of(book2));
    }

    @Test
    @DisplayName("장바구니에 도서 추가")
    void addCartItem() {
        CartRequest cartRequest = new CartRequest(user.getId(), book1.getId(), 100);
        Cart cart = Cart.builder()
            .user(user)
            .book(book1)
            .quantity(cartRequest.quantity())
            .build();

        ReflectionTestUtils.setField(cart, "id", 1L);

        when(cartRepository.save(ArgumentMatchers.any(Cart.class))).thenReturn(cart);

        CartResponse cartResponse = cartService.addCartItem(cartRequest);

        assertThat(cartResponse.id()).isEqualTo(1L);
        assertThat(cartResponse.userId()).isEqualTo(user.getId());
        assertThat(cartResponse.bookId()).isEqualTo(book1.getId());
        assertThat(cartResponse.quantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("장바구니 수량 변경")
    void updateCartQuantity() {
        CartRequest cartRequest = new CartRequest(user.getId(), book1.getId(), 100);
        Cart cart = Cart.builder()
            .user(user)
            .book(book1)
            .quantity(cartRequest.quantity())
            .build();

        ReflectionTestUtils.setField(cart, "id", 1L);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartUpdateRequest cartUpdateRequest = new CartUpdateRequest(99);
        CartResponse cartResponse = cartService.updateCartQuantity(cart.getId(), cartUpdateRequest);

        assertThat(cartResponse.id()).isEqualTo(1L);
        assertThat(cartResponse.userId()).isEqualTo(user.getId());
        assertThat(cartResponse.bookId()).isEqualTo(book1.getId());
        assertThat(cartResponse.quantity()).isEqualTo(99);
    }

    @Test
    @DisplayName("장바구니 목록 조회")
    void getCartItemsByUserId() {
        List<Cart> carts = new ArrayList<>();
        Cart cart1 = Cart.builder()
            .user(user)
            .book(book1)
            .quantity(100)
            .build();
        ReflectionTestUtils.setField(cart1, "id", 1L);

        Cart cart2 = Cart.builder()
            .user(user)
            .book(book2)
            .quantity(100)
            .build();
        ReflectionTestUtils.setField(cart2, "id", 2L);

        carts.add(cart1);
        carts.add(cart2);

        when(cartRepository.findByUserId(user.getId())).thenReturn(carts);

        List<CartResponse> responses = cartService.getCartItemsByUserId(user.getId());

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(1).id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Redis 캐시 hit 시 장바구니 조회")
    void getCartItemsByUserIdWithRedis() {
        String key = "cart:user:1";
        CartResponse cachedCart = CartResponse.from(
            Cart.builder().id(1L).user(user).book(book1).quantity(1).build()
        );

        when(hashOperations.values(key)).thenReturn(List.of(cachedCart));

        List<CartResponse> result = cartService.getCartItemsByUserId(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);

        verify(cartRepository, never()).findByUserId(any());
    }

    @Test
    @DisplayName("비회원 장바구니 목록 조회")
    void getCartItemsByGuest() {
        List<GuestCartRequest> guestCarts = List.of(
            new GuestCartRequest(book1.getId(), 100),
            new GuestCartRequest(book2.getId(), 200)
        );

        when(bookRepository.findAllById(anyList())).thenReturn(List.of(book1, book2));

        List<CartResponse> responses = cartService.getCartItemsByGuest(guestCarts);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).bookId()).isEqualTo(book1.getId());
        assertThat(responses.get(0).quantity()).isEqualTo(100);
        assertThat(responses.get(1).bookId()).isEqualTo(book2.getId());
        assertThat(responses.get(1).quantity()).isEqualTo(200);
    }

    @Test
    @DisplayName("비회원 장바구니에 존재하지 않는 도서 포함 시 예외 발생")
    void getCartItemsByGuestFailure() {
        List<GuestCartRequest> guestCarts = List.of(
            new GuestCartRequest(book1.getId(), 100),
            new GuestCartRequest(999L, 200)
        );

        when(bookRepository.findAllById(anyList())).thenReturn(List.of(book1));

        assertThatThrownBy(() -> cartService.getCartItemsByGuest(guestCarts))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessageContaining("존재하지 않는 도서입니다 id: ");
    }

    @Test
    @DisplayName("장바구니 전체 삭제 성공")
    void deleteCartItemsSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        cartService.deleteCartItems(1L);

        verify(cartRepository).deleteAllByUserId(1L);
        verify(redisTemplate).delete("cart:user:1");
    }

    @Test
    @DisplayName("장바구니 전체 삭제 실패")
    void deleteCartItemsFailure() {
        when(cartRepository.findById(0L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCartItems(0L))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User not found. ID: ");
    }

    @Test
    @DisplayName("장바구니 선택 삭제 성공")
    void deleteCartItemSuccess() {
        Cart cart = Cart.builder()
            .user(user)
            .book(book1)
            .quantity(100)
            .build();

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.deleteCartItem(1L);
    }

    @Test
    @DisplayName("장바구니 선택 삭제 실패")
    void deleteCartItemFailure() {
        when(cartRepository.findById(0L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.deleteCartItem(0L))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessageContaining("존재하지 않는 장바구니입니다 id: ");
    }
}
