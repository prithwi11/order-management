package com.example.ordermanagement.customer.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.ordermanagement.customer.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer createCustomer(String name, String email) {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "name", name);
        ReflectionTestUtils.setField(customer, "email", email);
        return customer;
    }

    @Test
    void shouldSaveCustomerAndGenerateIdAndTimestamp() {
        Customer customer = createCustomer("Rohan Sharma", "rohan@example.com");

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getCreatedAt()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("Rohan Sharma");
        assertThat(savedCustomer.getEmail()).isEqualTo("rohan@example.com");
    }

    @Test
    void shouldFindCustomerByEmail() {
        Customer customer = createCustomer("Priya Das", "priya@example.com");
        customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findByEmail("priya@example.com");

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getName()).isEqualTo("Priya Das");
        assertThat(foundCustomer.get().getEmail()).isEqualTo("priya@example.com");
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        Customer customer = createCustomer("Ananya Sen", "ananya@example.com");
        customerRepository.save(customer);

        boolean exists = customerRepository.existsByEmail("ananya@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = customerRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindCustomerById() {
        Customer customer = createCustomer("Vikram Roy", "vikram@example.com");
        Customer savedCustomer = customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getId()).isEqualTo(savedCustomer.getId());
        assertThat(foundCustomer.get().getEmail()).isEqualTo("vikram@example.com");
    }
}
