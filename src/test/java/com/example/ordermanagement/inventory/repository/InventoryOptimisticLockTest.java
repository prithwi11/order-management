package com.example.ordermanagement.inventory.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.ordermanagement.category.entity.Category;
import com.example.ordermanagement.category.repository.CategoryRepository;
import com.example.ordermanagement.inventory.entity.Inventory;
import com.example.ordermanagement.product.entity.Product;
import com.example.ordermanagement.product.repository.ProductRepository;

@SpringBootTest
public class InventoryOptimisticLockTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    private TransactionTemplate txTemplate;
    private Long inventoryId;

    @BeforeEach
    void setUp() {
        this.txTemplate = new TransactionTemplate(transactionManager);

        this.inventoryId = txTemplate.execute(status -> {
            Category category = categoryRepository.save(new Category("Laptop", "Laptop"));

            // 1. Set category BEFORE saving product so category_id is not null
            Product product = new Product("Asus Gen 4", "Laptop", "L-1001", new BigDecimal("499.99"), true);
            product.setCategory(category);
            product = productRepository.save(product);

            Inventory inventory = new Inventory(product, 10);
            inventory = inventoryRepository.save(inventory);
            return inventory.getId();
        });
    }

    @Test
    @DisplayName("Should throw ObjectOptimisticLockingFailureException when Transaction B commits stale version")
    void shouldDetectStaleUpdate() {
        // 1. Transaction A loads Inventory (version = 0)
        Inventory inventoryTxA = txTemplate.execute(status ->
                inventoryRepository.findById(inventoryId).orElseThrow()
        );
        assertThat(inventoryTxA.getVersion()).isEqualTo(0L); // 2. Fixed comma to dot

        // 2. Transaction B loads Inventory independently (version = 0)
        Inventory inventoryTxB = txTemplate.execute(status ->
                inventoryRepository.findById(inventoryId).orElseThrow()
        );
        assertThat(inventoryTxB.getVersion()).isEqualTo(0L);

        // 3. Transaction A increases stock by 5 and commits
        txTemplate.executeWithoutResult(status -> {
            inventoryTxA.increaseStock(5);
            inventoryRepository.save(inventoryTxA);
        });

        // 4. Verify DB now has quantity 15 and version 1
        Inventory afterTxA = inventoryRepository.findById(inventoryId).orElseThrow();
        assertThat(afterTxA.getQuantityOnHand()).isEqualTo(15);
        assertThat(afterTxA.getVersion()).isEqualTo(1L);

        // 5. Transaction B modifies its stale entity and attempts to commit
        assertThatThrownBy(() ->
            txTemplate.executeWithoutResult(status -> {
                inventoryTxB.increaseStock(20);
                inventoryRepository.save(inventoryTxB); // 3. Fixed 20 -> inventoryTxB
            })
        ).isInstanceOf(ObjectOptimisticLockingFailureException.class);

        // 6. Confirm Transaction B's update was rejected
        Inventory finalInventory = inventoryRepository.findById(inventoryId).orElseThrow(); // 4. Added .orElseThrow()
        assertThat(finalInventory.getQuantityOnHand()).isEqualTo(15);
        assertThat(finalInventory.getVersion()).isEqualTo(1L);
    }
}
