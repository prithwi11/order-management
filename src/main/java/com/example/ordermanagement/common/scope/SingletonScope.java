package com.example.ordermanagement.common.scope;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SingletonScope {
    public final String identifier = UUID.randomUUID().toString();

    public String getIdentifier() {
        return this.identifier;
    }
}