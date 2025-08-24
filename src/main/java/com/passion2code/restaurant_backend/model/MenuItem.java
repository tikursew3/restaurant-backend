package com.passion2code.restaurant_backend.model;

import com.passion2code.restaurant_backend.enums.Size;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_items")
@NoArgsConstructor @AllArgsConstructor @Builder
@Data
@ToString(exclude = {"category", "imageUrls"})
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique product code (SKU)
    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Base price (for this item/size)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Multiple images stored as simple strings in a join table
    @ElementCollection
    @CollectionTable(
            name = "menu_item_images",
            joinColumns = @JoinColumn(name = "menu_item_id")
    )
    @Column(name = "image_url", nullable = false)
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    // Optional size; use REGULAR if you don't need sizes
    @Enumerated(EnumType.STRING)
    private Size size;

    // Control visibility on the site
    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist
    void prePersist() {
        if (isActive == null) isActive = true;
    }
}
