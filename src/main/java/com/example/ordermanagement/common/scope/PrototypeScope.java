package com.example.ordermanagement.common.scope;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PrototypeScope {
    private final String identifier = UUID.randomUUID().toString();

    public String getIdentifier() {
        return this.identifier;
    }
}