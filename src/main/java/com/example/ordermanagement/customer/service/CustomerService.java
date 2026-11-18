package com.example.ordermanagement.customer.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.repository.CustomerRepository;
import com.example.ordermanagement.customer.exception.CustomerAlreadyExistsException;

import org.springframework.transaction.annotation.Transactional;

import com.example.ordermanagement.customer.exception.CustomerNotFoundException;
import java.util.List;

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
            throw new CustomerAlreadyExistsException("Customer with email " + email + " already exists.");
        }

        //Create Customer Object
        Customer newCustomer = new Customer(name, email);

        //Save
        return this.customerRepository.save(newCustomer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        Customer customer = this.customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

        return customer;
    }

    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return this.customerRepository.findAll(pageable);
    }

    @Transactional 
    public Customer updateCustomer(Long id, String name, String email) {
        Customer existingCustomer = this.customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        if (!existingCustomer.getEmail().equals(email)
                && customerRepository.existsByEmail(email)) {
            throw new CustomerAlreadyExistsException(
                "Customer already exists with email: " + email
            );
        }
        existingCustomer.setName(name);
        existingCustomer.setEmail(email);

        return existingCustomer;
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));

        customerRepository.delete(existingCustomer);
    }
}
