package com.passion2code.restaurant_backend.dto;

import com.passion2code.restaurant_backend.enums.Size;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class MenuItemResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Size size;
    private Boolean isActive;
    private Long categoryId;
    private String categoryName;
    private List<String> imageUrls;
}
