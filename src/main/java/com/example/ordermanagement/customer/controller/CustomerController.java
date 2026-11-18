package com.example.ordermanagement.customer.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ordermanagement.customer.dto.request.CreateCustomerRequest;
import com.example.ordermanagement.customer.dto.response.CustomerResponse;
import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping 
    public ResponseEntity<CustomerResponse> createCustomer(
        @Valid @RequestBody CreateCustomerRequest request) {
            Customer createdCustomer = customerService.createCustomer(request.name(), request.email());
            CustomerResponse response = CustomerResponse.from(createdCustomer);
            // return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
                
            return ResponseEntity.created(location).body(response);
        
        }
}
