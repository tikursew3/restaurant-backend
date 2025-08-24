package com.passion2code.restaurant_backend.controller;

import com.passion2code.restaurant_backend.dto.ImageUrlRequest;
import com.passion2code.restaurant_backend.dto.MenuItemRequest;
import com.passion2code.restaurant_backend.dto.MenuItemResponse;
import com.passion2code.restaurant_backend.model.MenuItem;
import com.passion2code.restaurant_backend.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(@RequestBody MenuItemRequest req) {
        MenuItem item = menuItemService.create(
                req.getSku(),
                req.getName(),
                req.getDescription(),
                req.getPrice(),
                req.getCategoryId(),
                req.getSize(),
                req.getImageUrls(),
                req.getIsActive()
        );
        return ResponseEntity.created(URI.create("/api/menu-items/" + item.getId()))
                .body(toResponse(item));
    }

    @PutMapping("/{id}")
    public MenuItemResponse update(@PathVariable Long id, @RequestBody MenuItemRequest req) {
        MenuItem item = menuItemService.update(
                id,
                req.getSku(),
                req.getName(),
                req.getDescription(),
                req.getPrice(),
                req.getCategoryId(),
                req.getSize(),
                req.getImageUrls(),
                req.getIsActive()
        );
        return toResponse(item);
    }

    @PatchMapping("/{id}/active")
    public MenuItemResponse toggleActive(@PathVariable Long id, @RequestParam("value") boolean value) {
        return toResponse(menuItemService.toggleActive(id, value));
    }

    @GetMapping("/{id}")
    public MenuItemResponse getById(@PathVariable Long id) {
        return toResponse(menuItemService.getById(id));
    }

    @GetMapping("/active")
    public List<MenuItemResponse> listActive() {
        return menuItemService.listActive().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/category/{categoryId}")
    public List<MenuItemResponse> listByCategory(@PathVariable Long categoryId) {
        return menuItemService.listByCategory(categoryId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @PostMapping("/{id}/images")
    public MenuItemResponse addImage(@PathVariable Long id, @RequestBody ImageUrlRequest req) {
        return toResponse(menuItemService.addImage(id, req.getImageUrl()));
    }

    @DeleteMapping("/{id}/images")
    public MenuItemResponse removeImage(@PathVariable Long id, @RequestBody ImageUrlRequest req) {
        return toResponse(menuItemService.removeImage(id, req.getImageUrl()));
    }

    private MenuItemResponse toResponse(MenuItem m) {
        return new MenuItemResponse(
                m.getId(),
                m.getSku(),
                m.getName(),
                m.getDescription(),
                m.getPrice(),
                m.getSize(),
                m.getIsActive(),
                m.getCategory() != null ? m.getCategory().getId() : null,
                m.getCategory() != null ? m.getCategory().getName() : null,
                m.getImageUrls()
        );
    }
}
