package com.example.ordermanagement.order.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.order.exception.InvalidOrderStateException;
import com.example.ordermanagement.orderitem.entity.OrderItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_reference", nullable = false, unique = true, length = 50)
    private String orderReference;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // --- Controlled Initialization ---
    public Order() {
        this.status = OrderStatus.PENDING;
    }

    public Order(Customer customer, String orderReference) {
        this.customer = customer;
        this.orderReference = orderReference;
        this.status = OrderStatus.PENDING;
        this.totalAmount = BigDecimal.ZERO;
    }

    // --- State Machine Domain Methods ---

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    "Cannot confirm order in status: " + this.status + ". Only PENDING orders can be confirmed.");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void startProcessing() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException(
                    "Cannot start processing order in status: " + this.status + ". Only CONFIRMED orders can be processed.");
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void ship() {
        if (this.status != OrderStatus.PROCESSING) {
            throw new InvalidOrderStateException(
                    "Cannot ship order in status: " + this.status + ". Only PROCESSING orders can be shipped.");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(
                    "Cannot deliver order in status: " + this.status + ". Only SHIPPED orders can be delivered.");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                    "Cannot cancel order once it has been " + this.status + ".");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already CANCELLED.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // --- Relationship Helpers ---

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
    }

    // --- Getters and Setters (setStatus intentionally omitted) ---

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderReference() {
        return this.orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return this.items;
    }
}
