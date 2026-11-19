package com.example.ordermanagement.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @NotBlank (message = "Name is required")
    @Size (max = 100, message = "Name must not exceed 100 characters")
    String name,

    @Size (max = 500, message = "Category must not exceed 500 characters")
    String description
) {

}
