package com.example.ordermanagement.category.dto.response;

import java.util.List;

import com.example.ordermanagement.category.entity.Category;

public record CategoryListResponse(
        List<CategoryResponse> content,
        int totalElements
) {
    public static CategoryListResponse from(List<Category> customers) {
        List<CategoryResponse> content = customers.stream()
                .map(CategoryResponse::from)
                .toList();

        return new CategoryListResponse(
                content,
                content.size()
        );
    }
}
