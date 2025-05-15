package shop.ink3.api.order.cart.service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.book.book.entity.Book;
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
    // TODO: private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    public CartResponse addCartItem(CartRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new UserNotFoundException(request.userId()));
        // TODO: Book book = bookRepository.findById(request.bookId()).orElseThrow(() -> new BookNotFoundException(request.bookId()));

        Cart cart = Cart.builder()
            .user(user)
            .book(null) // book
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
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException("존재하지 않는 장바구니입니다."));

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

    public void deleteCartItem(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new CartNotFoundException("존재하지 않는 장바구니입니다."));

        cartRepository.deleteById(id);

        String key = CART_KEY_PREFIX + cart.getUser().getId();
        hashOps().delete(key, id.toString());
    }

    private CartResponse toResponse(Cart cart) {
        return CartResponse.from(cart);
    }

    private HashOperations<String, String, CartResponse> hashOps() {
        return redisTemplate.opsForHash();
    }
}
