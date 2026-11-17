package com.example.ordermanagement.customer.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

public class CreateCustomerRequestTest {
    private static Validator validator;

    @BeforeAll 
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test 
    void shouldPassValidationWhenEmailAndNameValid() {
        CreateCustomerRequest request = new CreateCustomerRequest("John Doe", "john.doe@gmail.com");

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test 
    void shouldFailWhenNameisBlank() {
        CreateCustomerRequest request = new CreateCustomerRequest("", "john.doe@gmail.com");

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

      @Test
    void shouldFailValidationWhenNameIsLongerThan100Characters() {
        String longName = "a".repeat(101);
        CreateCustomerRequest request = new CreateCustomerRequest(longName, "rohan@example.com");

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void shouldFailValidationWhenEmailFormatIsInvalid() {
        CreateCustomerRequest request = new CreateCustomerRequest("Rohan Sharma", "invalid-email-format");

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shouldFailValidationWhenEmailIsBlank() {
        CreateCustomerRequest request = new CreateCustomerRequest("Rohan Sharma", "   ");

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shouldFailValidationWhenEmailIsLongerThan255Characters() {
        // Construct a syntactically valid email longer than 255 characters (e.g., 245 chars + "@example.com" = 257 chars)
        String longEmail = "a".repeat(245) + "@example.com";
        CreateCustomerRequest request = new CreateCustomerRequest("Rohan Sharma", longEmail);

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}
