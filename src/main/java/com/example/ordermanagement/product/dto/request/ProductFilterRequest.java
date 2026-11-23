package com.example.ordermanagement.product.dto.request;

import java.math.BigDecimal;

public record ProductFilterRequest(
    String search,
    Long categoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Boolean active,
    String sortBy,
    String sortDirection,
    Integer page,
    Integer size
) {
} 