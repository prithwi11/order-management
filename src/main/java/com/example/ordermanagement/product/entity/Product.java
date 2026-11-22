package com.example.ordermanagement.product.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.example.ordermanagement.category.entity.Category;

import jakarta.persistence.*;

@Entity 
@Table (name = "products")
public class Product {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, length = 150)
    private String name;

    @Column (name = "description", length = 1000)
    private String description;

    @Column (name = "sku", nullable = false, length = 50, unique = true)
    private String sku;

    @Column (name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column (name = "active", nullable = false)
    private Boolean active;

    @CreationTimestamp 
    @Column (name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public Product() {}

    public Product(String name, String description, String sku, BigDecimal price, Boolean active) {
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.price = price;
        this.active = active;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSku() {
        return this.sku;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
