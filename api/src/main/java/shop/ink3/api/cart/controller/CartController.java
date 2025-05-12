package shop.ink3.api.cart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shop.ink3.api.cart.dto.CartRequest;
import shop.ink3.api.cart.dto.CartResponse;
import shop.ink3.api.cart.service.CartService;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addCart(@RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addCart(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CartResponse>> getCarts(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }
}
