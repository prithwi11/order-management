package com.example.ordermanagement.common.lifecycle;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class LifecycleDependency {
    private final String identifier = UUID.randomUUID().toString();
    
    public String getIdentifier() {
        return this.identifier;
    }
}