package com.example.ordermanagement.product.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.example.ordermanagement.product.entity.Product;

public record ProductResponse(
    Long id,
    String name,
    String description,
    String sku,
    BigDecimal price,
    Boolean active,
    Instant createdAt,
    CategorySummaryResponse category
) {
    public static ProductResponse from (Product product) {
        CategorySummaryResponse categoryResponse = new CategorySummaryResponse(
            product.getCategory().getId(),
            product.getCategory().getName()
        );

        return new ProductResponse(
            product.getId(), 
            product.getName(), 
            product.getDescription(), product.getSku(), 
            product.getPrice(), 
            product.getActive(), 
            product.getCreatedAt(),
            categoryResponse
        );
    }

}
