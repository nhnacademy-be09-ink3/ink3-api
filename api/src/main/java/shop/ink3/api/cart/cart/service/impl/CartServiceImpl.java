package shop.ink3.api.cart.cart.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.cart.cart.dto.CartRequest;
import shop.ink3.api.cart.cart.dto.CartResponse;
import shop.ink3.api.cart.cart.entity.Cart;
import shop.ink3.api.cart.cart.repository.CartRepository;
import shop.ink3.api.cart.cart.service.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public CartResponse addCart(CartRequest request) {
        Cart cart = CartRequest.toEntity(request);
        return toResponse(cartRepository.save(cart));
    }

    @Override
    public List<CartResponse> getCartsByUserId(Long userId) {
        return cartRepository.findByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    private CartResponse toResponse(Cart cart) {
        return new CartResponse(cart.getId(), cart.getUserId(), cart.getBookId(), cart.getQuantity());
    }
}
