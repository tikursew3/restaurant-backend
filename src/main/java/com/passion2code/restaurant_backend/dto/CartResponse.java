package com.passion2code.restaurant_backend.dto;


import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponse {
    private String cartToken;
    private String status;
    private List<CartItemResponse> items;
    private Integer itemCount;
    private BigDecimal subtotal;
}
