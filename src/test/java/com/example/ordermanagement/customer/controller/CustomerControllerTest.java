package com.example.ordermanagement.customer.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ordermanagement.customer.entity.Customer;
import com.example.ordermanagement.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.ordermanagement.customer.dto.request.CreateCustomerRequest;
import com.example.ordermanagement.customer.dto.request.CreateCustomerRequestTest;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerAndReturn201WithLocationHeader() throws Exception {
        // Arrange
        CreateCustomerRequest request = new CreateCustomerRequest("Rohan Sharma", "rohan@example.com");

        Customer savedCustomer = new Customer("Rohan Sharma", "rohan@example.com");
        ReflectionTestUtils.setField(savedCustomer, "id", 1L);
        ReflectionTestUtils.setField(savedCustomer, "createdAt", Instant.parse("2026-11-17T07:00:00Z"));

        when(customerService.createCustomer("Rohan Sharma", "rohan@example.com"))
                .thenReturn(savedCustomer);

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/customers/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rohan Sharma"))
                .andExpect(jsonPath("$.email").value("rohan@example.com"))
                .andExpect(jsonPath("$.createdAt").value("2026-11-17T07:00:00Z"));

        verify(customerService).createCustomer("Rohan Sharma", "rohan@example.com");
    }

    @Test
    void shouldReturn400WhenRequestPayloadIsInvalid() throws Exception {
        // Arrange: Blank name and invalid email format
        CreateCustomerRequest invalidRequest = new CreateCustomerRequest("", "not-an-email");

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Service should never be called when @Valid rejects the payload
        verifyNoInteractions(customerService);
    }
}
