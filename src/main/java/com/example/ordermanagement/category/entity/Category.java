package com.example.ordermanagement.category.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

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
}
