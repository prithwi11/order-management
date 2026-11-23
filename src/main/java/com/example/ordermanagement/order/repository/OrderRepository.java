package com.example.ordermanagement.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ordermanagement.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerId(Pageable pageable, Long customerId);
    Page<Order> findByStatus(Pageable pageable, String status);
    @EntityGraph(attributePaths = {"items", "customer"})
    Optional<Order> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"items", "customer"})
    Optional<Order> findByOrderReference(String orderReference);

}
