package com.example.ordermanagement.category.dto.response;

import java.time.Instant;

import com.example.ordermanagement.category.entity.Category;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    Instant createdAt
) {
    public static CategoryResponse from (Category category) {
        return new CategoryResponse(
            category.getId(), 
            category.getName(),
            category.getDescription(),
            category.getCreatedAt()
        );
    }
} 
