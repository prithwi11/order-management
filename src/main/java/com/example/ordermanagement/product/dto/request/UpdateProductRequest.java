package com.example.ordermanagement.product.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record UpdateProductRequest(
    @NotBlank(message = "Product name is required")
    @Size (max = 100, message = "Product name must not exceed 150 characters")
    String name,

    @NotBlank (message = "Category id is required")
    Long categoryId,
    
    @Size (max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotBlank (message = "sku is required")
    @Size (max = 50, message = "sku must not exceed 50 characters")
    String sku,

    @NotBlank (message = "price is required")
    @DecimalMin (value = "0.00", inclusive = true, message = "Price cannot be negative")
    @Digits (integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 decimal places")
    BigDecimal price,

    @NotBlank (message = "Product active status is required")
    Boolean active
) {
} 