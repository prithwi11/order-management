package com.example.ordermanagement.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordermanagement.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @EntityGraph (attributePaths = "category")
    Optional<Product> findWithCategoryById(Long id);

    @EntityGraph(attributePaths = "category")
    Page<Product> findAllBy(Pageable pageable);

    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);

} 
