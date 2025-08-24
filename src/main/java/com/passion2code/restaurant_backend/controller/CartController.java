package com.passion2code.restaurant_backend.controller;

import com.passion2code.restaurant_backend.dto.CartItemRequest;
import com.passion2code.restaurant_backend.dto.CartItemResponse;
import com.passion2code.restaurant_backend.dto.CartResponse;
import com.passion2code.restaurant_backend.model.Cart;
import com.passion2code.restaurant_backend.model.CartItem;
import com.passion2code.restaurant_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Create or resume a cart (returns a cartToken if you don't have one yet)
    @PostMapping("/init")
    public ResponseEntity<CartResponse> init(@RequestParam(required = false) String cartToken) {
        Cart cart = cartService.getOrCreateCart(cartToken);
        Cart withItems = cartService.getActiveCartWithItems(cart.getCartToken());
        return ResponseEntity.ok(toResponse(withItems));
    }

    // Get current cart by token
    @GetMapping
    public CartResponse get(@RequestParam String cartToken) {
        return toResponse(cartService.getActiveCartWithItems(cartToken));
    }

    // Add an item (quantity > 0)
    @PostMapping("/items")
    public CartResponse addItem(@RequestParam String cartToken, @RequestBody CartItemRequest body) {
        cartService.addItem(cartToken, body.getMenuItemId(), body.getQuantity());
        return toResponse(cartService.getActiveCartWithItems(cartToken));
    }

    // Update quantity (quantity <= 0 removes the line)
    @PatchMapping("/items")
    public CartResponse updateItem(@RequestParam String cartToken, @RequestBody CartItemRequest body) {
        cartService.updateItem(cartToken, body.getMenuItemId(), body.getQuantity());
        return toResponse(cartService.getActiveCartWithItems(cartToken));
    }

    // Remove one item
    @DeleteMapping("/items")
    public CartResponse removeItem(@RequestParam String cartToken, @RequestBody CartItemRequest body) {
        cartService.removeItem(cartToken, body.getMenuItemId());
        return toResponse(cartService.getActiveCartWithItems(cartToken));
    }

    // Clear the cart
    @DeleteMapping
    public CartResponse clear(@RequestParam String cartToken) {
        cartService.clearCart(cartToken);
        return toResponse(cartService.getActiveCartWithItems(cartToken));
    }

    // Subtotal only (optional helper)
    @GetMapping("/total")
    public BigDecimal total(@RequestParam String cartToken) {
        return cartService.computeTotal(cartToken);
    }

    // -------- mapping helpers --------
    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int count = items.stream().mapToInt(CartItemResponse::getQuantity).sum();
        return new CartResponse(
                cart.getCartToken(),
                cart.getStatus().name(),
                items,
                count,
                subtotal
        );
    }

    private CartItemResponse toItemResponse(CartItem ci) {
        return new CartItemResponse(
                ci.getMenuItem().getId(),
                ci.getMenuItem().getSku(),
                ci.getMenuItem().getName(),
                ci.getMenuItem().getSize(),
                ci.getUnitPrice(),
                ci.getQuantity(),
                ci.getLineTotal(),
                ci.getMenuItem().getImageUrls()
        );
    }
}
