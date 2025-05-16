package shop.ink3.api.order.cart.service;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.book.book.entity.Book;
import shop.ink3.api.book.book.repository.BookRepository;
import shop.ink3.api.book.common.exception.BookNotFoundException;
import shop.ink3.api.order.cart.dto.CartRequest;
import shop.ink3.api.order.cart.dto.CartResponse;
import shop.ink3.api.order.cart.dto.CartUpdateRequest;
import shop.ink3.api.order.cart.entity.Cart;
import shop.ink3.api.order.cart.repository.CartRepository;
import shop.ink3.api.order.common.exception.CartNotFoundException;
import shop.ink3.api.user.user.entity.User;
import shop.ink3.api.user.user.exception.UserNotFoundException;
import shop.ink3.api.user.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private static final String CART_KEY_PREFIX = "cart:user:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    public CartResponse addCartItem(CartRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        Book book = bookRepository.findById(request.bookId()).orElseThrow(() -> new BookNotFoundException(request.bookId()));

        Cart cart = Cart.builder()
            .user(user)
            .book(book)
            .quantity(request.quantity())
            .build();
        Cart savedCart = cartRepository.save(cart);

        CartResponse response = toResponse(savedCart);
        String key = CART_KEY_PREFIX + savedCart.getUser().getId();
        hashOps().put(key, savedCart.getId().toString(), response);
        redisTemplate.expire(key, Duration.ofDays(3));

        return response;
    }

    public CartResponse updateCartQuantity(Long cartId, CartUpdateRequest request) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));

        cart.updateQuantity(request.quantity());
        cartRepository.save(cart);

        return toResponse(cart);
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCartItemsByUserId(Long userId) {
        String key = CART_KEY_PREFIX + userId;
        List<CartResponse> cacheCarts = hashOps().values(key);
        if (!cacheCarts.isEmpty()) {
            return cacheCarts;
        }

        List<CartResponse> carts = cartRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();

        for (CartResponse response : carts) {
            hashOps().put(key, response.id().toString(), response);
        }
        redisTemplate.expire(key, Duration.ofDays(3));

        return carts;
    }

    // public List<CartResponse> getCartItemsByGuest(List<GuestCartRequest> requests) {
    //     List<Long> bookIds = requests.stream()
    //         .map(GuestCartRequest::bookId)
    //         .toList();
    //
    //     List<Book> books = bookRepository.findAllById(bookIds);
    //     Map<Long, Book> bookMap = books.stream()
    //         .collect(Collectors.toMap(Book::getId, Function.identity()));
    //
    //     return requests.stream()
    //         .map(req -> {
    //             Book book = bookMap.get(req.bookId());
    //             if (book == null) {
    //                 throw new BookNotFoundException("존재하지 않는 도서입니다.");
    //             }
    //             Cart cart = new Cart(null, book, req.quantity());
    //             return toResponse(cart);
    //         })
    //         .toList();
    // }

    public void deleteCartItem(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException(cartId));

        cartRepository.deleteById(cartId);

        String key = CART_KEY_PREFIX + cart.getUser().getId();
        hashOps().delete(key, cartId.toString());
    }

    public void deleteCartItems(Long userId) {
        cartRepository.deleteAllByUserId(userId);
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }

    private CartResponse toResponse(Cart cart) {
        return CartResponse.from(cart);
    }

    private HashOperations<String, String, CartResponse> hashOps() {
        return redisTemplate.opsForHash();
    }
}
