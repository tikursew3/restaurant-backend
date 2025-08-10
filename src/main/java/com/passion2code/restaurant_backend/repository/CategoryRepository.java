package com.passion2code.restaurant_backend.repository;

import com.passion2code.restaurant_backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
