package com.example.ordermanagement.category.service;

import org.springframework.stereotype.Service;

import com.example.ordermanagement.category.exception.CategoryAlreadyExistsException;
import com.example.ordermanagement.category.exception.CategoryNotFoundException;
import com.example.ordermanagement.category.repository.CategoryRepository;
import com.example.ordermanagement.category.entity.Category;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service 
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional 
    public  Category createCategory(String name, String description) {
        // Check if category exists
        boolean isCategoryExists = this.categoryRepository.existsByName(name);
        if (isCategoryExists) {
            throw new CategoryAlreadyExistsException("Category " + name + " alredy exists");
        }

        Category newCategory = new Category(name, description);

        return this.categoryRepository.save(newCategory);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        Category category = this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        return category;
    }

    @Transactional (readOnly=true)
    public List<Category> getAllCategories() {
        List<Category> categories = this.categoryRepository.findAll();
        return categories;
    }

    @Transactional
    public Category updateCategory(String name, String description, Long id) {
        Category existingCategory = this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        if (!existingCategory.getName().equals(name)
        && categoryRepository.existsByName(name)) {
            throw new CategoryAlreadyExistsException(
                "Category " + name + " already exists"
            );
        }
        existingCategory.setDescription(description);
        existingCategory.setName(name);

        return existingCategory;
    }

    @Transactional 
    public void deleteCategory(Long id) {
        Category existingCategory = this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        this.categoryRepository.delete(existingCategory);
    }

    
}
