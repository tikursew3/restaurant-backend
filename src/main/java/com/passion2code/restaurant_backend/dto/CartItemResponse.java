package com.passion2code.restaurant_backend.dto;

import com.passion2code.restaurant_backend.enums.Size;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartItemResponse {
    private Long menuItemId;
    private String sku;
    private String name;
    private Size size;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal lineTotal;
    private List<String> imageUrls; // already eagerly fetched
}
