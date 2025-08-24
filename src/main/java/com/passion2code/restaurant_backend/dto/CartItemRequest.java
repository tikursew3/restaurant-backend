package com.passion2code.restaurant_backend.dto;

import lombok.*;

@Data
public class CartItemRequest {
    private Long menuItemId;
    private Integer quantity; // > 0 to add/update; ignored for remove
}
