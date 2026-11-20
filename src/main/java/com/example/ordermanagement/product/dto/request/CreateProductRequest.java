package com.example.ordermanagement.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record CreateProductRequest(
    @NotBlank(message = "Product name is required")
    @Size (max = 150, message = "Product name must not exceed 150 characters")
    String name,

    @NotNull  (message = "Category id is required")
    Long categoryId,
    
    @Size (max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotBlank (message = "sku is required")
    @Size (max = 50, message = "sku must not exceed 50 characters")
    String sku,

    @NotNull (message = "price is required")
    @DecimalMin (value = "0.01", inclusive = true, message = "Price cannot be negative")
    @Digits (integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 decimal places")
    BigDecimal price,

    @NotNull (message = "Product active status is required")
    Boolean active
) {
} 