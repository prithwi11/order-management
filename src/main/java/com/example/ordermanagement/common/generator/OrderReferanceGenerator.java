package com.example.ordermanagement.common.generator;

public class OrderReferanceGenerator {
    public String generate() {
        return "ORD" + System.currentTimeMillis();
    }
}