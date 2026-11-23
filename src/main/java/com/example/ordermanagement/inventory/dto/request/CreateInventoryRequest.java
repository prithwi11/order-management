package com.example.ordermanagement.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateInventoryRequest(
    @NotNull (message = "Product ID is required")
    @Positive (message = "Product ID must be positive")
    Long productId,

    @NotNull (message = "Initial quantity is required")
    @Min (value = 0, message = "Initial quantity cannot be negative")
    Integer initialQuantity
) {} 
