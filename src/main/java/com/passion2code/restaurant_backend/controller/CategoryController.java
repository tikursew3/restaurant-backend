package com.passion2code.restaurant_backend.controller;

import com.passion2code.restaurant_backend.dto.CategoryRequest;
import com.passion2code.restaurant_backend.dto.CategoryResponse;
import com.passion2code.restaurant_backend.model.Category;
import com.passion2code.restaurant_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest req) {
        Category c = categoryService.create(req.getName(), req.getDescription());
        return ResponseEntity.created(URI.create("/api/categories/" + c.getId()))
                .body(toResponse(c));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @RequestBody CategoryRequest req) {
        Category c = categoryService.update(id, req.getName(), req.getDescription());
        return toResponse(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable Long id) {
        return toResponse(categoryService.getById(id));
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categoryService.listAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
