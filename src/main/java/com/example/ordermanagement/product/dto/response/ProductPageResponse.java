package com.example.ordermanagement.product.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;
import com.example.ordermanagement.product.entity.Product;

public record ProductPageResponse(
        List<ProductResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static ProductPageResponse from(Page<Product> productPage) {
        List<ProductResponse> content = productPage.getContent()
            .stream()
            .map(ProductResponse::from)
            .toList();

        return new ProductPageResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }
}
