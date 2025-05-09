package shop.ink3.api.cart.cart.service;

import java.util.List;

import shop.ink3.api.cart.cart.dto.CartRequest;
import shop.ink3.api.cart.cart.dto.CartResponse;

public interface CartService {
    CartResponse addCart(CartRequest request);
    List<CartResponse> getCartsByUserId(Long userId);
    void deleteCart(Long id);
}
