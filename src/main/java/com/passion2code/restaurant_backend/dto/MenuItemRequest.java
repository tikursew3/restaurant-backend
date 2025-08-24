package com.passion2code.restaurant_backend.dto;

import com.passion2code.restaurant_backend.enums.Size;
import lombok.*;


import java.math.BigDecimal;
import java.util.List;

@Data
public class MenuItemRequest {
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Size size;                // optional
    private List<String> imageUrls;   // optional
    private Boolean isActive;         // optional (defaults true)
}
