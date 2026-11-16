package com.example.ordermanagement.common.service;

import com.example.ordermanagement.common.generator.OrderReferanceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class ReferanceTestService {

    private final OrderReferanceGenerator orderReferanceGenerator;

    public ReferanceTestService(
        @Qualifier("standardReferanceGenerator")
        OrderReferanceGenerator orderReferanceGenerator) {
        this.orderReferanceGenerator = orderReferanceGenerator;
    }

    public String generateTest() {
        return this.orderReferanceGenerator.generate();
    }
}
