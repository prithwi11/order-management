package com.example.ordermanagement.category.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.ordermanagement.product.entity.Product;

import jakarta.persistence.*;

@Entity 
@Table (name = "categories")
public class Category {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column (name = "description", length = 500)
    private String description;

    @CreationTimestamp 
    @Column (name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany (mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description= description;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (this.products.contains(product)) {
            return;
        }

        Category previousCategory = product.getCategory();

        if (previousCategory != null && previousCategory != this) {
            previousCategory.products.remove(product);
        }

        this.products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (!this.products.contains(product)) {
            return;
        }

        this.products.remove(product);

        if (product.getCategory() == this) {
            product.setCategory(null);
        }
    }

}
