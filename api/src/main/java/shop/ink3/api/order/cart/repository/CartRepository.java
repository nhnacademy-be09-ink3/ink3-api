package shop.ink3.api.order.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.ink3.api.order.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
