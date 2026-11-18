package com.example.ordermanagement.customer.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.ordermanagement.customer.entity.Customer;

public record CustomerPageResponse(
        List<CustomerResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static CustomerPageResponse from(Page<Customer> customerPage) {
        List<CustomerResponse> content = customerPage.getContent()
            .stream()
            .map(CustomerResponse::from)
            .toList();

        return new CustomerPageResponse(
                content,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isLast()
        );
    }
}
