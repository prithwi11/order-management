package com.example.ordermanagement.customer.service;

import org.springframework.stereotype.Service;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.repository.CustomerRepository;

import jakarta.transaction.Transactional;

@Service 
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional 
    public Customer createCustomer(String name, String email) {
        // Check if email exists
        boolean isEmailExists = this.customerRepository.existsByEmail(email);

        if (isEmailExists) {
            throw new IllegalArgumentException("Customer with email " + email + " already exists.");
        }

        //Create Customer Object
        Customer newCustomer = new Customer(name, email);

        //Save
        return this.customerRepository.save(newCustomer);
    }
}
