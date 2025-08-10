package com.passion2code.restaurant_backend.repository;

import com.passion2code.restaurant_backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndMenuItemId(Long cartId, Long menuItemId);
    void deleteByCartIdAndMenuItemId(Long cartId, Long menuItemId);
}
