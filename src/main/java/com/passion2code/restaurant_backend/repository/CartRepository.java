package com.passion2code.restaurant_backend.repository;

import com.passion2code.restaurant_backend.enums.CartStatus;
import com.passion2code.restaurant_backend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCartTokenAndStatus(String cartToken, CartStatus status);
    Optional<Cart> findByCartToken(String cartToken);
}
