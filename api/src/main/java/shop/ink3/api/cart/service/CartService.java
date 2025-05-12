package shop.ink3.api.cart.service;

import java.util.List;

import shop.ink3.api.cart.dto.CartRequest;
import shop.ink3.api.cart.dto.CartResponse;

public interface CartService {
    CartResponse addCart(CartRequest request);
    List<CartResponse> getCartsByUserId(Long userId);
    void deleteCart(Long id);
}
