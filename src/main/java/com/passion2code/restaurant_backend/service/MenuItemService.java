package com.passion2code.restaurant_backend.service;

import com.passion2code.restaurant_backend.enums.Size;
import com.passion2code.restaurant_backend.model.Category;
import com.passion2code.restaurant_backend.model.MenuItem;
import com.passion2code.restaurant_backend.repository.CategoryRepository;
import com.passion2code.restaurant_backend.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;

    // ---------- Create ----------
    @Transactional
    public MenuItem create(String sku,
                           String name,
                           String description,
                           BigDecimal price,
                           Long categoryId,
                           Size size,
                           List<String> imageUrls,
                           Boolean isActive) {

        validateCommonFields(sku, name, price, categoryId);

        if (menuItemRepository.findAll().stream().anyMatch(mi -> mi.getSku().equalsIgnoreCase(sku))) {
            throw new IllegalArgumentException("SKU already exists: " + sku);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        MenuItem item = MenuItem.builder()
                .sku(sku.trim())
                .name(name.trim())
                .description(description)
                .price(price)
                .category(category)
                .size(size)                 // can be null; enum is optional in your entity
                .imageUrls(imageUrls != null ? new ArrayList<>(imageUrls) : new ArrayList<>())
                .isActive(isActive != null ? isActive : Boolean.TRUE)
                .build();

        return menuItemRepository.save(item);
    }

    // ---------- Update ----------
    @Transactional
    public MenuItem update(Long id,
                           String sku,
                           String name,
                           String description,
                           BigDecimal price,
                           Long categoryId,
                           Size size,
                           List<String> imageUrls,
                           Boolean isActive) {

        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));

        if (sku != null && !sku.isBlank()) {
            // Ensure uniqueness if changed
            if (!sku.equalsIgnoreCase(item.getSku()) &&
                    menuItemRepository.findAll().stream().anyMatch(mi -> mi.getSku().equalsIgnoreCase(sku))) {
                throw new IllegalArgumentException("SKU already exists: " + sku);
            }
            item.setSku(sku.trim());
        }
        if (name != null && !name.isBlank()) item.setName(name.trim());
        if (description != null) item.setDescription(description);
        if (price != null) {
            if (price.signum() < 0) throw new IllegalArgumentException("Price must be >= 0");
            item.setPrice(price);
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
            item.setCategory(category);
        }
        if (size != null) item.setSize(size);
        if (imageUrls != null) {
            item.getImageUrls().clear();
            item.getImageUrls().addAll(imageUrls);
        }
        if (isActive != null) item.setIsActive(isActive);

        return menuItemRepository.save(item);
    }

    // ---------- Quick helpers ----------
    @Transactional
    public MenuItem toggleActive(Long id, boolean active) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
        item.setIsActive(active);
        return menuItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public MenuItem getById(Long id) {
        return menuItemRepository.findWithDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<MenuItem> listActive() {
        return menuItemRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<MenuItem> listByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }

    @Transactional
    public void delete(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Menu item not found: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    // ---------- Image utilities (optional but handy) ----------
    @Transactional
    public MenuItem addImage(Long id, String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl is required");
        }
        MenuItem item = getById(id);
        item.getImageUrls().add(imageUrl.trim());
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem removeImage(Long id, String imageUrl) {
        MenuItem item = getById(id);
        item.getImageUrls().removeIf(u -> u.equalsIgnoreCase(imageUrl));
        return menuItemRepository.save(item);
    }

    // ---------- Private ----------
    private void validateCommonFields(String sku, String name, BigDecimal price, Long categoryId) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("SKU is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must be >= 0");
        if (categoryId == null) throw new IllegalArgumentException("Category ID is required");
    }



}
