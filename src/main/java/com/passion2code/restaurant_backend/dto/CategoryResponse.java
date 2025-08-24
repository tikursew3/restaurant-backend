package com.passion2code.restaurant_backend.dto;


import lombok.*;


@Data
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
}
