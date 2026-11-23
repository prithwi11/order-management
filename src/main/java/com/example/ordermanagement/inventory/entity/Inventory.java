package com.example.ordermanagement.inventory.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.example.ordermanagement.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity 
@Table (name = "inventories")
public class Inventory {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull 
    @Min (0)
    @Column (name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand = 0;

    @NotNull 
    @Min (0)
    @Column (name = "quantity_reserved", nullable = false)
    private Integer quantityReserve = 0;

    @Version 
    private Long version;

    @CreationTimestamp 
    @Column (name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "product_id", nullable = false, unique = true)
    private Product product;

    protected Inventory() {}

    public Inventory(Product product, Integer initialQuantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (initialQuantity == null || initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be null or negative");
        }
        this.product = product;
        this.quantityReserve = 0;
        this.quantityOnHand = initialQuantity;
    }

    @Transient  
    public Integer getQuantityAvailable() {
        return this.quantityOnHand - this.quantityReserve;
    }

    public Long getId() {
        return this.id;
    }

    public Integer getQuantityOnHand() {
        return this.quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getquantityReserve() {
        return this.quantityReserve;
    }

    public void setquantityReserve(Integer quantityReserve) {
        this.quantityReserve = quantityReserve;
    }

    public Long getVersion() {
        return this.version;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        this.quantityOnHand+= quantity;
    }

    public void decreaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        if (this.getQuantityAvailable() < quantity) {
            throw new IllegalStateException("Insufficient stock available");
        }
        this.quantityOnHand -= quantity;
    }

    public void reserveStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        if (this.getQuantityAvailable() < quantity) {
            throw new IllegalStateException("Insufficient stock available");
        }
        this.quantityReserve += quantity;
    }

    public void releaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        if (this.getquantityReserve() < quantity) {
            throw new IllegalStateException("Cannot release more than current reserved");
        }
        this.quantityReserve -= quantity;
    }

    public void consumeReserveStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number");
        }
        if (this.getquantityReserve() < quantity) {
            throw new IllegalStateException("Cannot release more than current reserved");
        }
        this.quantityOnHand -= quantity;
        this.quantityReserve -= quantity;
    }

}
