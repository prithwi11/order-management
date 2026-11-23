package com.example.ordermanagement.inventory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.ordermanagement.category.entity.Category;
import com.example.ordermanagement.inventory.entity.Inventory;
import com.example.ordermanagement.product.entity.Product;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true"
})
public class InventoryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    private TransactionTemplate txTemplate;

    private Long savedInventoryId;
    private Long savedProductId;
    private  Long inventoryId;

    @BeforeEach
    void setUp() {
        Category category = new Category("Laptop", "Electronic items");
        entityManager.persist(category);

        Product productA = new Product("Asus gen 4", "Laptop of asus", "L-1001", new BigDecimal("499.99"), true);
        category.addProduct(productA);
        entityManager.persist(productA);

        Inventory inventoryA = new Inventory(productA, 10);
        productA.addInventory(inventoryA);
        entityManager.persist(inventoryA);

        // Flush inserts to DB and clear 1st-level cache so queries hit the DB
        entityManager.flush();
        entityManager.clear();

        this.savedInventoryId = inventoryA.getId();
        this.savedProductId = productA.getId();
    }

    @Test
    @DisplayName("1. findById() + access only getQuantityOnHand()")
    void testFindByIdAndAccessQuantityOnly() {
        System.out.println("=== START SQL: findById() ===");

        Inventory loadInventory = inventoryRepository.findById(savedInventoryId).orElseThrow();
        Integer qty = loadInventory.getQuantityOnHand();

        System.out.println("=== END SQL: findById() ===");

        assertThat(qty).isEqualTo(10);
    }

    @Test
    @DisplayName("2. findByProductId() + access getProduct().getName()")
    void testFindByProductIdAndAccessProductName() {
        System.out.println("=== START SQL: findByProductId() ===");

        Inventory loadInventory = inventoryRepository.findByProductId(savedProductId).orElseThrow();
        String productName = loadInventory.getProduct().getName();

        System.out.println("=== END SQL: findByProductId() ===");

        assertThat(productName).isEqualTo("Asus gen 4");
    }

    @Test 
    @DisplayName ("3. Concurrency COntrol")
    void testByOptimisticControl() {
        Inventory loadInventory = inventoryRepository.findById(savedInventoryId).orElseThrow();
        Long version = loadInventory.getVersion();

        loadInventory.increaseStock(10);
        entityManager.flush();
        assertThat(loadInventory.getVersion()).isGreaterThan(version);
    }
}
