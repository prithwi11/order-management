package com.example.ordermanagement.customer.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ordermanagement.common.exception.GlobalExceptionHandler;
import com.example.ordermanagement.customer.exception.CustomerAlreadyExistsException;
import com.example.ordermanagement.customer.service.CustomerService;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    @DisplayName("Should return 400 Bad Request with field validation errors when name or email is invalid")
    void createCustomer_InvalidInput_Returns400() throws Exception {
        String invalidJson = """
            {
                "name": "",
                "email": "not-a-valid-email"
            }
            """;

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.path").value("/api/customers"))
            .andExpect(jsonPath("$.timestamp").isNotEmpty())
            .andExpect(jsonPath("$.validationErrors.name").isNotEmpty())
            .andExpect(jsonPath("$.validationErrors.email").isNotEmpty());

        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when JSON body is malformed")
    void createCustomer_MalformedJson_Returns400() throws Exception {
        String malformedJson = "{ \"name\": \"Alice\", \"email\": ";

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("Malformed or unreadable request body."))
            .andExpect(jsonPath("$.path").value("/api/customers"))
            .andExpect(jsonPath("$.validationErrors").value(nullValue()));

        verifyNoInteractions(customerService);
    }

    @Test
    @DisplayName("Should return 409 Conflict with business message when customer already exists")
    void createCustomer_DuplicateCustomer_Returns409() throws Exception {
        String validJson = """
            {
                "name": "Alice",
                "email": "alice@example.com"
            }
            """;
        String businessMessage = "Customer with email alice@example.com already exists.";

        when(customerService.createCustomer(anyString(), anyString()))
            .thenThrow(new CustomerAlreadyExistsException(businessMessage));

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.message").value(businessMessage))
            .andExpect(jsonPath("$.path").value("/api/customers"))
            .andExpect(content().string(not(containsString("CustomerAlreadyExistsException"))))
            .andExpect(content().string(not(containsString("com.example.ordermanagement"))));
    }

    @Test
    @DisplayName("Should return 409 Conflict with generic message on database constraint violation")
    void createCustomer_DatabaseConstraintConflict_Returns409() throws Exception {
        String validJson = """
            {
                "name": "Alice",
                "email": "alice@example.com"
            }
            """;

        when(customerService.createCustomer(anyString(), anyString()))
            .thenThrow(new DataIntegrityViolationException("ERROR: duplicate key value violates unique constraint \"uk_customer_email\""));

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"))
            .andExpect(jsonPath("$.message").value("The request conflicts with existing data."))
            .andExpect(jsonPath("$.path").value("/api/customers"))
            .andExpect(content().string(not(containsString("uk_customer_email"))))
            .andExpect(content().string(not(containsString("duplicate key value"))));
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error with generic message on unexpected exception")
    void createCustomer_UnexpectedException_Returns500() throws Exception {
        String validJson = """
            {
                "name": "Alice",
                "email": "alice@example.com"
            }
            """;
        String secretInternalError = "Database connection pool exhausted on node db-prod-01";

        when(customerService.createCustomer(anyString(), anyString()))
            .thenThrow(new RuntimeException(secretInternalError));

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andExpect(jsonPath("$.message").value("An unexpected internal error occurred."))
            .andExpect(jsonPath("$.path").value("/api/customers"))
            .andExpect(content().string(not(containsString(secretInternalError))))
            .andExpect(content().string(not(containsString("RuntimeException"))));
    }
}
