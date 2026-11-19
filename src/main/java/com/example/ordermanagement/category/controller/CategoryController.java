package com.example.ordermanagement.category.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ordermanagement.category.dto.request.CreateCategoryRequest;
import com.example.ordermanagement.category.dto.response.CategoryListResponse;
import com.example.ordermanagement.category.dto.response.CategoryResponse;
import com.example.ordermanagement.category.entity.Category;
import com.example.ordermanagement.category.service.CategoryService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping 
    public ResponseEntity<CategoryResponse> createCategory(
        @Valid @RequestBody CreateCategoryRequest request
    ) {
        Category createdCategory = categoryService.createCategory(request.name(), request.description());
        CategoryResponse response = CategoryResponse.from(createdCategory);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdCategory.getId())
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping 
    public ResponseEntity<CategoryListResponse> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(CategoryListResponse.from(categories));
    }

    @GetMapping ("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(CategoryResponse.from(category));
    } 

    @PutMapping ("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody CreateCategoryRequest request
    ) {
        Category updateCategory = categoryService.updateCategory(request.name(), request.description(), id);
        return ResponseEntity.ok(CategoryResponse.from(updateCategory));
    }

    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
