package com.passion2code.restaurant_backend.service;

import com.passion2code.restaurant_backend.enums.CartStatus;
import com.passion2code.restaurant_backend.model.Cart;
import com.passion2code.restaurant_backend.model.CartItem;
import com.passion2code.restaurant_backend.model.MenuItem;
import com.passion2code.restaurant_backend.repository.CartItemRepository;
import com.passion2code.restaurant_backend.repository.CartRepository;
import com.passion2code.restaurant_backend.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;

    /** Get existing ACTIVE cart by token or create a new one. */
    @Transactional
    public Cart getOrCreateCart(String cartToken) {
        if (cartToken != null && !cartToken.isBlank()) {
            Optional<Cart> found = cartRepository.findByCartTokenAndStatus(cartToken, CartStatus.ACTIVE);
            if (found.isPresent()) return found.get();
        }
        // When cartToken is null, Cart.@PrePersist will generate one
        Cart cart = Cart.builder()
                .cartToken(cartToken)
                .status(CartStatus.ACTIVE)
                .build();
        return cartRepository.save(cart);
    }

    /** Fetch ACTIVE cart (basic) */
    @Transactional(readOnly = true)
    public Cart getActiveCart(String cartToken) {
        return cartRepository.findByCartTokenAndStatus(cartToken, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found for token"));
    }

    /** Fetch ACTIVE cart with items and product images eagerly (avoids LazyInitialization). */
    @Transactional(readOnly = true)
    public Cart getActiveCartWithItems(String cartToken) {
        return cartRepository.findWithItemsByCartTokenAndStatus(cartToken, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found for token"));
    }

    /** Add a menu item or increase quantity if already present. */
    @Transactional
    public Cart addItem(String cartToken, Long menuItemId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Cart cart = getOrCreateCart(cartToken);
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));

        if (Boolean.FALSE.equals(menuItem.getIsActive())) {
            throw new IllegalStateException("Menu item is inactive");
        }

        Optional<CartItem> existing = cartItemRepository.findByCartIdAndMenuItemId(cart.getId(), menuItemId);
        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + quantity);
            cartItemRepository.save(ci);
            return cart;
        }

        CartItem ci = CartItem.builder()
                .cart(cart)
                .menuItem(menuItem)
                .quantity(quantity)
                .unitPrice(menuItem.getPrice()) // snapshot price
                .build();

        cart.getItems().add(ci);              // cascade from Cart -> CartItem
        return cartRepository.save(cart);
    }

    /** Set quantity (<=0 removes the line). */
    @Transactional
    public Cart updateItem(String cartToken, Long menuItemId, int quantity) {
        Cart cart = getActiveCart(cartToken);
        CartItem ci = cartItemRepository.findByCartIdAndMenuItemId(cart.getId(), menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(ci);
            cartItemRepository.delete(ci);
        } else {
            ci.setQuantity(quantity);
            cartItemRepository.save(ci);
        }
        return cart;
    }

    /** Remove one menu item from cart. */
    @Transactional
    public Cart removeItem(String cartToken, Long menuItemId) {
        Cart cart = getActiveCart(cartToken);
        cartItemRepository.findByCartIdAndMenuItemId(cart.getId(), menuItemId)
                .ifPresent(ci -> {
                    cart.getItems().remove(ci);
                    cartItemRepository.delete(ci);
                });
        return cart;
    }

    /** Clear all items from the cart. */
    @Transactional
    public Cart clearCart(String cartToken) {
        Cart cart = getActiveCart(cartToken);
        cart.getItems().clear();              // orphanRemoval=true will delete rows
        return cartRepository.save(cart);
    }

    /** Compute subtotal from snapshot prices. */
    @Transactional(readOnly = true)
    public BigDecimal computeTotal(String cartToken) {
        Cart cart = getActiveCart(cartToken);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            BigDecimal line = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(line);
        }
        return total;
    }
}
