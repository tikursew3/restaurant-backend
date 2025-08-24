package com.passion2code.restaurant_backend.repository;

import com.passion2code.restaurant_backend.model.MenuItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // SINGLE item with category + imageUrls eagerly loaded
    @EntityGraph(attributePaths = {"category", "imageUrls"})
    Optional<MenuItem> findWithDetailsById(Long id);

    // LISTS with category + imageUrls eagerly loaded
    @EntityGraph(attributePaths = {"category", "imageUrls"})
    List<MenuItem> findByIsActiveTrue();

    @EntityGraph(attributePaths = {"category", "imageUrls"})
    List<MenuItem> findByCategoryIdAndIsActiveTrue(Long categoryId);

    // helpers
    Optional<MenuItem> findBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);
}
