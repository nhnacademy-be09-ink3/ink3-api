package shop.ink3.api.order.cart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import shop.ink3.api.common.dto.CommonResponse;
import shop.ink3.api.order.cart.dto.CartRequest;
import shop.ink3.api.order.cart.dto.CartResponse;
import shop.ink3.api.order.cart.dto.CartUpdateRequest;
import shop.ink3.api.order.cart.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    /**
     * Adds a new item to the shopping cart.
     *
     * @param request the cart item details to add, validated for required constraints
     * @return a response entity containing the created cart item wrapped in a common response, with HTTP status 201 Created
     */
    @PostMapping
    public ResponseEntity<CommonResponse<CartResponse>> addCart(@RequestBody @Valid CartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.create(cartService.addCartItem(request)));
    }

    /**
     * Updates the quantity of a specific cart item.
     *
     * @param cartId the ID of the cart item to update
     * @param request the request containing the new quantity
     * @return a response entity containing the updated cart information
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<CommonResponse<CartResponse>> updateQuantity(@PathVariable Long cartId, @RequestBody @Valid CartUpdateRequest request) {
        return ResponseEntity.ok(CommonResponse.update(cartService.updateCartQuantity(cartId, request)));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<List<CartResponse>>> getCarts(@PathVariable Long userId) {
        return ResponseEntity.ok(CommonResponse.success(cartService.getCartItemsByUserId(userId)));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteCarts(@PathVariable Long userId) {
        cartService.deleteCartItems(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
