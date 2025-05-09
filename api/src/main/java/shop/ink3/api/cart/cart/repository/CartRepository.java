package shop.ink3.api.cart.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.ink3.api.cart.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);
}
