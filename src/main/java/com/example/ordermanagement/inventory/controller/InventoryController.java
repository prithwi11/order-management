package com.example.ordermanagement.inventory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ordermanagement.inventory.dto.request.CreateInventoryRequest;
import com.example.ordermanagement.inventory.dto.response.InventoryResponse;
import com.example.ordermanagement.inventory.dto.request.StockUpdateRequest;
import com.example.ordermanagement.inventory.entity.Inventory;
import com.example.ordermanagement.inventory.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody CreateInventoryRequest request) {
        Inventory created = inventoryService.createInventory(request.productId(), request.initialQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryResponse.fromEntity(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
        Inventory inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(InventoryResponse.fromEntity(inventory));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(InventoryResponse.fromEntity(inventory));
    }

    @PatchMapping("/{id}/increase")
    public ResponseEntity<InventoryResponse> increaseStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        Inventory updated = inventoryService.increaseStock(id, request.quantity());
        return ResponseEntity.ok(InventoryResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/decrease")
    public ResponseEntity<InventoryResponse> decreaseStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        Inventory updated = inventoryService.decreaseStock(id, request.quantity());
        return ResponseEntity.ok(InventoryResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        Inventory updated = inventoryService.reserveStock(id, request.quantity());
        return ResponseEntity.ok(InventoryResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        Inventory updated = inventoryService.releaseStock(id, request.quantity());
        return ResponseEntity.ok(InventoryResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/consume")
    public ResponseEntity<InventoryResponse> consumeStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest request) {
        Inventory updated = inventoryService.consumInventory(id, request.quantity());
        return ResponseEntity.ok(InventoryResponse.fromEntity(updated));
    }
}
