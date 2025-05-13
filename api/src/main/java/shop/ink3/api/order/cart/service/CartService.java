package shop.ink3.api.order.cart.service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.order.cart.dto.CartRequest;
import shop.ink3.api.order.cart.dto.CartResponse;
import shop.ink3.api.order.cart.entity.Cart;
import shop.ink3.api.order.cart.repository.CartRepository;
import shop.ink3.api.order.common.exception.CartNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private static final String CART_KEY_PREFIX = "cart:user:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final CartRepository cartRepository;

    public CartResponse addCartItem(CartRequest request) {
        Cart cart = CartRequest.toEntity(request);
        Cart savedCart = cartRepository.save(cart);

        CartResponse response = toResponse(savedCart);
        String key = CART_KEY_PREFIX + savedCart.getUser().getId();
        hashOps().put(key, savedCart.getId().toString(), response);
        redisTemplate.expire(key, Duration.ofDays(3));

        return response;
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
        return new CartResponse(cart.getId(), cart.getUser(), cart.getBook(), cart.getQuantity());
    }

    private HashOperations<String, String, CartResponse> hashOps() {
        return redisTemplate.opsForHash();
    }
}
