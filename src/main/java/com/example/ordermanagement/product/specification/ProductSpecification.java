package com.example.ordermanagement.product.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.example.ordermanagement.product.entity.Product;

public class ProductSpecification {
    public static Specification<Product> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String keyword = "%" + search.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(
                    cb.lower(root.get("name")),
                    keyword
                ),
                cb.like(
                    cb.lower(root.get("sku")), 
                    keyword
                )
            );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                root.get("price"), 
                minPrice
            );
        };
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                root.get("price"), 
                maxPrice
            );
        };
    }

    public static Specification<Product> hasActiveStatus(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("active"), active);
        };
    }
    
}
