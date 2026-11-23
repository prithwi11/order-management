package com.example.ordermanagement.product.service;

import java.math.BigDecimal;
import java.util.Map;

import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ordermanagement.category.entity.Category;
import com.example.ordermanagement.category.exception.CategoryNotFoundException;
import com.example.ordermanagement.category.repository.CategoryRepository;
import com.example.ordermanagement.product.dto.request.ProductFilterRequest;
import com.example.ordermanagement.product.dto.response.ProductResponse;
import com.example.ordermanagement.product.entity.Product;
import com.example.ordermanagement.product.exception.ProductAlreadyExistsException;
import com.example.ordermanagement.product.exception.ProductNotFoundException;
import com.example.ordermanagement.product.repository.ProductRepository;
import com.example.ordermanagement.product.specification.ProductSpecification;

@Service 
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    // Allowed sorting fields
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "name", "name",
        "price", "price",
        "sku", "sku",
        "id", "id"
    );

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional  
    public ProductResponse createProducts(String name, Long categoryId, String description, String sku, BigDecimal price, Boolean active) {
        if (this.productRepository.existsBySku(sku)) {
            throw new ProductAlreadyExistsException("Product already exists with sku " + sku);
        }

        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("No category found"));

        Product newProduct = new Product(name, description, sku, price, active);
        newProduct.setCategory(category);
        
        Product savedProduct = this.productRepository.save(newProduct);
        return ProductResponse.from(savedProduct); // Category is already in memory
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return this.productRepository.findWithCategoryById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Transactional(readOnly = true) 
    public Page<ProductResponse> fetchAllProduct(ProductFilterRequest request) {
        
        Specification<Product> spec = ProductSpecification.hasActiveStatus(request.active())
            .and(ProductSpecification.search(request.search()))
            .and(ProductSpecification.hasCategory(request.categoryId()))
            .and(ProductSpecification.minPrice(request.minPrice()))
            .and(ProductSpecification.maxPrice(request.maxPrice()));

        Sort sort = buildSort(
            request.sortBy(),
            request.sortDirection()
        );

        int page = request.page() == null ? 0 : request.page();
        int size = request.size() == null ? 20 : request.size();

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Invalid pagination parametres");
        }
        
        Pageable pageable = PageRequest.of(
            request.page() == null ? 0 : request.page(), 
            request.size() == null ? 20 : request.size(),
            sort
        );

        return productRepository.findAll(spec, pageable).map(ProductResponse::from);
    } 

    @Transactional 
    public ProductResponse updateProduct(Long id, String name, Long categoryId, String description, String sku, BigDecimal price, boolean active) {
        Product existingProduct = this.productRepository.findWithCategoryById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        Category existingCategory = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        if (!existingProduct.getSku().equals(sku) && productRepository.existsBySku(sku)) {
            throw new ProductAlreadyExistsException("Product already exists with sku: " + sku);
        }

        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setSku(sku);
        existingProduct.setPrice(price);
        existingProduct.setActive(active);
        existingCategory.addProduct(existingProduct);

        return ProductResponse.from(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        productRepository.delete(existingProduct);
    }

    private Sort buildSort(String sortBy, String direction) {
        String field = SORT_FIELDS.get(
            sortBy == null ? "id" : sortBy.trim().toLowerCase()
        );

        if (field == null) {
            throw new IllegalArgumentException("Unsupported sort field");
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC: Sort.Direction.ASC;

        return Sort.by(
            new Sort.Order(sortDirection, field),
            new Sort.Order(Sort.Direction.ASC, "id")
        );
    }
}
