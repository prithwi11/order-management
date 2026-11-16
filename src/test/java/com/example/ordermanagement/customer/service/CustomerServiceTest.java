package com.example.ordermanagement.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(CustomerService.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityManager entityManager;

    // Test A — Successful registration
    @Test
    void shouldCreateCustomerSuccessfully() {
        String name = "Rohan Sharma";
        String email = "rohan.service@example.com";

        Customer createdCustomer = customerService.createCustomer(name, email);

        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getId()).isNotNull();
        assertThat(createdCustomer.getName()).isEqualTo(name);
        assertThat(createdCustomer.getEmail()).isEqualTo(email);
        assertThat(createdCustomer.getCreatedAt()).isNotNull();
    }

    // Test B — Duplicate email
    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {
        // 1. Register a customer
        String duplicateEmail = "priya.service@example.com";
        customerService.createCustomer("Priya Das", duplicateEmail);

        // 2 & 3. Attempt to register another customer using the same email and verify rejection
        assertThatThrownBy(() -> customerService.createCustomer("Another Priya", duplicateEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(duplicateEmail);
    }

    // Test C — Persistence
    @Test
    void shouldPersistCustomerInDatabaseAfterClearingPersistenceContext() {
        // 1. Register a customer through the service
        Customer savedCustomer = customerService.createCustomer("Ananya Sen", "ananya.persist@example.com");

        // 2. Flush pending SQL and clear the persistence context (first-level cache)
        entityManager.flush();
        entityManager.clear();

        // 3. Retrieve the customer through the repository (forces a fresh SELECT query)
        Optional<Customer> retrievedCustomer = customerRepository.findById(savedCustomer.getId());

        // 4. Verify that the record exists in the database with matching fields
        assertThat(retrievedCustomer).isPresent();
        assertThat(retrievedCustomer.get().getId()).isEqualTo(savedCustomer.getId());
        assertThat(retrievedCustomer.get().getName()).isEqualTo("Ananya Sen");
        assertThat(retrievedCustomer.get().getEmail()).isEqualTo("ananya.persist@example.com");
        assertThat(retrievedCustomer.get().getCreatedAt()).isNotNull();
    }
}
