package com.example.ordermanagement.order.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
    @NotNull (message = "customer id is required")
    @Positive (message = "Please provide valid customer id")
    Long customerId,

    @NotEmpty (message = "Order must contain atleast one item")
    @Valid  
    List<CreateOrderItemRequest> items

) {
    public record CreateOrderItemRequest(
        @NotNull (message = "Please provide product id")
        @Positive (message = "Please provide valid value in product id")
        Long productId,

        @NotNull (message = "Quantity is required")
        @Min (value = 1, message = "Please provide valid value in quantity")
        Integer quantity
    ) {
    }
}
