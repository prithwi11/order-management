package com.example.ordermanagement.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.ordermanagement.category.repository.CategoryRepository;
import com.example.ordermanagement.product.entity.Product; // Update package if needed

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("findByCategoryId fetches only products belonging to that category")
    void shouldFindProductsByCategoryId() {
        Long categoryId = 1L;

        List<Product> products = productRepository.findByCategoryId(categoryId);

        assertThat(products)
                .isNotEmpty()
                .allSatisfy(product -> 
                    assertThat(product.getCategory().getId()).isEqualTo(categoryId)
                    // Use product.getCategoryId() instead if your entity maps the raw FK column
                );

        assertThat(productRepository.findByCategoryId(-1L)).isEmpty();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase matches substring regardless of case")
    void shouldFindProductsByNameContainingIgnoreCase() {
        String searchTerm = "pro";

        List<Product> products = this.productRepository.findByNameContainingIgnoreCase(searchTerm);

        assertThat(products)
                .isNotEmpty()
                .allSatisfy(product -> 
                    assertThat(product.getName()).containsIgnoringCase(searchTerm)
                );

        assertThat(productRepository.findByNameContainingIgnoreCase("ZZZZ_NON_EXISTENT_999")).isEmpty();
    }

    @Test
    @DisplayName("findByActiveTrue fetches only active products")
    void shouldFindOnlyActiveProducts() {
        List<Product> activeProducts = productRepository.findByActiveTrue();

        assertThat(activeProducts)
                .isNotEmpty()
                .allMatch(product -> Boolean.TRUE.equals(product.getActive())); // Use Product::getActive if boxed Boolean
    }

    @Test
    @DisplayName("findByCategoryIdAndActiveTrue fetches only active products in that category")
    void shouldFindActiveProductsByCategoryId() {
        Long categoryId = 1L;

        List<Product> activeCategoryProducts = productRepository.findByCategoryIdAndActiveTrue(categoryId);

        assertThat(activeCategoryProducts)
                .isNotEmpty()
                .allSatisfy(product -> {
                    assertThat(product.getCategory().getId()).isEqualTo(categoryId);
                    assertThat(product.getActive()).isTrue();
                });
    }
}
