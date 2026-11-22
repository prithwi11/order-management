package com.example.ordermanagement.product;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.ordermanagement.category.entity.Category;
import com.example.ordermanagement.category.repository.CategoryRepository;
import com.example.ordermanagement.product.entity.Product;
import com.example.ordermanagement.product.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

@DataJpaTest 
@AutoConfigureTestDatabase (replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductCategoryTest {
    @Autowired 
    private EntityManager entityManager;

    @Autowired 
    private CategoryRepository categoryRepository;

    @Autowired 
    private ProductRepository productRepository;

    @Test 
    void changingOnlyInverseSideDoesnotPersistForeignKey() {
        Category categoryA = categoryRepository.save(new Category("Category A", "Category A description"));

        Category categoryB = categoryRepository.save(new Category("Category B", "Category B description"));

        // Create and save product to category a
        Product product = new Product("Product Test", "Product Description", "sku-001", new BigDecimal(100.01), true);
        product.setCategory(categoryA);
        productRepository.save(product);

        //Flush initial insert to database
        entityManager.flush();

        //Clear the persistence context
        entityManager.clear();

        // Load the product and Category B from the database
        Product loadedProduct = productRepository.findById(product.getId()).orElseThrow();
        Category loadedCategoryB = categoryRepository.findById(categoryB.getId()).orElseThrow();

        // CHange only the inverse collection without calling
        loadedCategoryB.getProducts().add(loadedProduct);

        //Flush the persistence context
        entityManager.flush();

        // Clear and reload the product
        entityManager.clear();
        Product reloadedProduct = productRepository.findById(product.getId()).orElseThrow();

        // Verify which category the product actually belongs to
        assertThat(reloadedProduct.getCategory().getId()).isEqualTo(categoryA.getId());
        assertThat(reloadedProduct.getCategory().getName()).isEqualTo(categoryA.getName());
    }

    @Test
    void shouldUpdateForeignKeyWhenOnlyOwningSideIsModified() {
        // 1. Arrange: Persist Category A, Category B, and a Product linked to Category A
        Category categoryA = new Category("Electronics", "");
        Category categoryB = new Category("Home Appliances", "");
        entityManager.persist(categoryA);
        entityManager.persist(categoryB);

        Product product = new Product("Smart Toaster", "2-slice toaster", "SKU-101", new BigDecimal("49.99"), true);
        product.setCategory(categoryA);
        entityManager.persist(product);

        entityManager.flush();
        entityManager.clear();

        // 2. Act: Load the product and Category B, then update ONLY the owning side
        Product loadedProduct = entityManager.find(Product.class, product.getId());
        Category loadedCategoryB = entityManager.find(Category.class, categoryB.getId());

        loadedProduct.setCategory(loadedCategoryB);
        // Intentionally NOT calling loadedCategoryB.getProducts().add(loadedProduct)

        entityManager.flush(); // Executes UPDATE product SET category_id = ?
        entityManager.clear(); // Evicts cached entities so the next find() hits the database

        // 3. Assert: Reload the product from the DB and verify the new category
        Product reloadedProduct = entityManager.find(Product.class, product.getId());
        assertThat(reloadedProduct.getCategory().getId()).isEqualTo(categoryB.getId());
        assertThat(reloadedProduct.getCategory().getName()).isEqualTo("Home Appliances");
    }

    @Test 
    void shouldPersistChildSideWhenParentisPersisted() {
        Category category = new Category("Health Supplements", "");
        Product product = new Product("Protein Bar", "200gm Protein Bar", "SKU-101", new BigDecimal("49.99"), true);
        // product.setCategory(category);
        category.addProduct(product);
        entityManager.persist(category);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldRemoveChildWhenParentRemoveisPersisted() {
        Category category = new Category("Daily Products", "");
        entityManager.persist(category);

        Product productA = new Product("Water Bottle", "1l Water bottle", "SKU-004", new BigDecimal("19.99"), true);
        Product productB = new Product("Mortein", "Mosquito Killer", "SKU-005", new BigDecimal("9.99"), true);

        category.addProduct(productA);
        category.addProduct(productB);
        entityManager.persist(productA);
        entityManager.persist(productB);

        entityManager.flush();
        entityManager.clear();

        Category reloadCategory = entityManager.find(Category.class, category.getId());
        entityManager.remove(reloadCategory);
        entityManager.flush();

        Product reloadProduct = entityManager.find(Product.class, productA.getId());
        assertThat(reloadProduct.getName()).isEqualTo("Water Bottle");
    }

    @Test
    void shouldDeleteProductWhenRemovedFromCategoryCollection() {

        Category category = new Category("Electronics", "Electronic products");

        Product productA = new Product(
                "Laptop",
                "Business laptop",
                "SKU-201",
                new BigDecimal("999.99"),
                true
        );

        Product productB = new Product(
                "Keyboard",
                "Mechanical keyboard",
                "SKU-202",
                new BigDecimal("79.99"),
                true
        );

        category.addProduct(productA);
        category.addProduct(productB);

        entityManager.persist(category);
        entityManager.persist(productA);
        entityManager.persist(productB);

        entityManager.flush();
        entityManager.clear();

        Category managedCategory =
                entityManager.find(Category.class, category.getId());

        Product managedProduct =
                entityManager.find(Product.class, productA.getId());

        managedCategory.getProducts().remove(managedProduct);

        entityManager.flush();
        entityManager.clear();

        Product deletedProduct =
                entityManager.find(Product.class, productA.getId());

        Product remainingProduct =
                entityManager.find(Product.class, productB.getId());

        assertThat(deletedProduct).isNull();
        assertThat(remainingProduct).isNotNull();
    }

    @Test
    void shouldPreventCategoryDeletionWhenProductsStillReferenceIt() {

        Category category = new Category("Electronics", "Electronic products");
        entityManager.persist(category);

        Product product = new Product(
                "Laptop",
                "Business laptop",
                "SKU-301",
                new BigDecimal("999.99"),
                true
        );

        product.setCategory(category);
        entityManager.persist(product);

        entityManager.flush();
        entityManager.clear();

        Category managedCategory =
                entityManager.find(Category.class, category.getId());

        assertThatThrownBy(() -> {
            entityManager.remove(managedCategory);
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }
    
}
