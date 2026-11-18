package com.example.ordermanagement.customer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.exception.CustomerAlreadyExistsException;
import com.example.ordermanagement.customer.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Should create and save customer when email is unique")
    void createCustomer_UniqueEmail_SavesCustomer() {
        String name = "Alice";
        String email = "alice@example.com";
        Customer savedCustomer = new Customer(name, email);

        when(customerRepository.existsByEmail(email)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = customerService.createCustomer(name, email);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw CustomerAlreadyExistsException when email already exists")
    void createCustomer_DuplicateEmail_ThrowsCustomerAlreadyExistsException() {
        String name = "Alice";
        String email = "alice@example.com";

        when(customerRepository.existsByEmail(email)).thenReturn(true);

        CustomerAlreadyExistsException exception = assertThrows(
            CustomerAlreadyExistsException.class,
            () -> customerService.createCustomer(name, email)
        );

        assertEquals("Customer with email " + email + " already exists.", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
