package com.example.ordermanagement.customer.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.ordermanagement.customer.dto.request.CreateCustomerRequest;
import com.example.ordermanagement.customer.dto.request.UpdateCustomerRequest;
import com.example.ordermanagement.customer.dto.response.CustomerPageResponse;
import com.example.ordermanagement.customer.dto.response.CustomerResponse;
import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.service.CustomerService;

import io.micrometer.core.ipc.http.HttpSender.Response;
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

    @GetMapping
    public ResponseEntity<CustomerPageResponse> getAllCustomers(
    @PageableDefault (size = 10, sort = "id") Pageable pageable) {
        Page<Customer> customerPage = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(CustomerPageResponse.from(customerPage));   
    }

    @GetMapping ("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @PutMapping ("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCustomerRequest request
    )   {
            Customer updatCustomer = customerService.updateCustomer(id, request.name(), request.email());
            return ResponseEntity.ok(CustomerResponse.from(updatCustomer));
        }
    
        @DeleteMapping ("/{id}")
        public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        }

}
