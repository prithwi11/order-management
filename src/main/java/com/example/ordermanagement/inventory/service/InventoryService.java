package com.example.ordermanagement.inventory.service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ordermanagement.inventory.entity.Inventory;
import com.example.ordermanagement.inventory.exception.InventoryAlreadyExistsException;
import com.example.ordermanagement.inventory.exception.InventoryNotFoundException;
import com.example.ordermanagement.inventory.repository.InventoryRepository;
import com.example.ordermanagement.product.entity.Product;
import com.example.ordermanagement.product.exception.ProductNotFoundException;
import com.example.ordermanagement.product.repository.ProductRepository;


@Service 
public class InventoryService {
    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public Inventory getInventoryById(Long inventoryId) {
        return this.inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));
    }

    @Transactional(readOnly = true)
    public Inventory getInventoryByProductId(Long productId) {
        return this.inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));
    }

    @Transactional
    public Inventory createInventory(Long productId, Integer initialQuantity) {
        if (this.inventoryRepository.existsByProductId(productId)) {
            throw new InventoryAlreadyExistsException("Inventory already exists");
        }

        // Fetch once instead of calling existsById() + findById()
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product doesn't exist"));

        Inventory inventory = new Inventory(product, initialQuantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional 
    public Inventory increaseStock(Long inventoryId, Integer quantity) {
        // Check if inventory exists
        Inventory inventory = this.inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.increaseStock(quantity);
        return inventory;
    }

    @Transactional 
    public Inventory decreaseStock(Long inventoryId, Integer quantity) {
        // Check if inventory exists
        Inventory inventory = this.inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.decreaseStock(quantity);
        return inventory;
    }

    @Transactional 
    public Inventory reserveStock(Long inventoryId, Integer quantity) {
        // Check if inventory exists
        Inventory inventory = this.inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.reserveStock(quantity);
        return inventory;
    }

    @Transactional 
    public Inventory releaseStock(Long inventoryId, Integer quantity) {
        // Check if inventory exists
        Inventory inventory = this.inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.releaseStock(quantity);
        return inventory;
    }

    @Transactional 
    public Inventory consumInventory(Long inventoryId, Integer quantity) {
        // Check if inventory exists
        Inventory inventory = this.inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.consumeReserveStock(quantity);
        return inventory;
    }

    
    @Transactional
    public void reserveStockBulk(Map<Long, Integer> productQuantities) {
        if (productQuantities == null || productQuantities.isEmpty()) {
            throw new IllegalArgumentException("At least one product is required");
        }

        Map<Long, Inventory> inventoriesByProductId =
            inventoryRepository.findAllByProductIdIn(productQuantities.keySet())
                .stream()
                .collect(Collectors.toMap(
                    inventory -> inventory.getProduct().getId(),
                    Function.identity()
                ));

        if (inventoriesByProductId.size() != productQuantities.size()) {
            throw new InventoryNotFoundException(
                "Inventory records are missing for one or more products"
            );
        }

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Inventory inventory = inventoriesByProductId.get(entry.getKey());
            inventory.reserveStock(entry.getValue());
        }
    }

    @Transactional
    public void consumeStockBulk(Map<Long, Integer> productQuantities) {
        if (productQuantities == null || productQuantities.isEmpty()) {
            return;
        }

        Map<Long, Inventory> inventoriesByProductId = this.inventoryRepository
                .findAllByProductIdIn(productQuantities.keySet())
                .stream()
                .collect(Collectors.toMap(
                        inv -> inv.getProduct().getId(),
                        Function.identity()
                ));

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Inventory inventory = inventoriesByProductId.get(productId);
            if (inventory == null) {
                throw new InventoryNotFoundException("Inventory record not found for product id: " + productId);
            }
            inventory.consumeReserveStock(entry.getValue());
        }
    }

    @Transactional
    public void releaseStockBulk(Map<Long, Integer> productQuantities) {
        if (productQuantities == null || productQuantities.isEmpty()) {
            return;
        }

        Map<Long, Inventory> inventoriesByProductId = this.inventoryRepository
                .findAllByProductIdIn(productQuantities.keySet())
                .stream()
                .collect(Collectors.toMap(
                        inv -> inv.getProduct().getId(),
                        Function.identity()
                ));

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Inventory inventory = inventoriesByProductId.get(productId);
            if (inventory == null) {
                throw new InventoryNotFoundException("Inventory record not found for product id: " + productId);
            }
            inventory.releaseStock(entry.getValue());
        }
    }

}
