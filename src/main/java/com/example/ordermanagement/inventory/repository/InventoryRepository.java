package com.example.ordermanagement.inventory.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordermanagement.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @EntityGraph (attributePaths = "product")
    Optional<Inventory> findById(Long id);
    
    @EntityGraph (attributePaths = "product")
    Optional<Inventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);
    List<Inventory> findAllByProductIdIn(Collection<Long> productIds);
} 