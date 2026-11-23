package com.example.ordermanagement.inventory.dto.response;

import java.time.Instant;
import com.example.ordermanagement.inventory.entity.Inventory;

public record InventoryResponse(
    Long id,
    Long productId,
    String productName,
    String productSku,
    Integer quantityOnHand,
    Integer quantityOnReserve,
    Integer quantityAvailable,
    Instant createdAt
) {
    public static InventoryResponse fromEntity(Inventory inventory) {
        return new InventoryResponse(
            inventory.getId(),
            inventory.getProduct().getId(),
            inventory.getProduct().getName(),
            inventory.getProduct().getSku(),
            inventory.getQuantityOnHand(),
            inventory.getquantityReserve(),
            inventory.getQuantityAvailable(),
            inventory.getCreatedAt()
        );
    }
}
