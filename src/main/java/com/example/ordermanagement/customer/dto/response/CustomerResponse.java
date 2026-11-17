package com.example.ordermanagement.customer.dto.response;

import java.time.Instant;

import com.example.ordermanagement.customer.entity.Customer;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    Instant createdAt
) {
    public static CustomerResponse from (Customer customer) {
        return new CustomerResponse(
            customer.getId(), 
            customer.getName(), 
            customer.getEmail(), 
            customer.getCreatedAt()
        );
    }
}
