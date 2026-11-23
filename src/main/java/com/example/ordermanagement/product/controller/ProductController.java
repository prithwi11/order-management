package com.example.ordermanagement.product.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ordermanagement.product.dto.request.CreateProductRequest;
import com.example.ordermanagement.product.dto.request.ProductFilterRequest;
import com.example.ordermanagement.product.dto.request.UpdateProductRequest;
import com.example.ordermanagement.product.dto.response.ProductPageResponse;
import com.example.ordermanagement.product.dto.response.ProductResponse;
import com.example.ordermanagement.product.service.ProductService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping 
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse response = this.productService.createProducts(
            request.name(), 
            request.categoryId(), 
            request.description(), 
            request.sku(), 
            request.price(), 
            request.active()
        );

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id()) // Use response.getId() if ProductResponse is a class
            .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}") 
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = this.productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ProductPageResponse> getAllProduct(
        @Valid @ModelAttribute ProductFilterRequest request) {
        Page<ProductResponse> productPage = this.productService.fetchAllProduct(request);
        return ResponseEntity.ok(ProductPageResponse.from(productPage));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id,
        @Valid @RequestBody UpdateProductRequest request
    ) {
        ProductResponse updatedProduct = productService.updateProduct(
            id, 
            request.name(), 
            request.categoryId(), 
            request.description(), 
            request.sku(), 
            request.price(), 
            request.active()
        );
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
