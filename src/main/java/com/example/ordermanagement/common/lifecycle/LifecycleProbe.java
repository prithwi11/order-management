package com.example.ordermanagement.common.lifecycle;

import org.springframework.stereotype.Component;
import com.example.ordermanagement.common.lifecycle.LifecycleDependency;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class LifecycleProbe {
    private LifecycleDependency lifecycleDependency;
    public Boolean isPostConstruct = false;

    public LifecycleProbe(LifecycleDependency lifecycleDependency) {
        this.lifecycleDependency = lifecycleDependency;
        System.out.println("Constructor initialized");
    }

    @PostConstruct
    public void methodPostConstruct() {
        this.isPostConstruct = true;
        String identifierPost = this.lifecycleDependency.getIdentifier();
        System.out.println("Identifier Post Construct : " +  identifierPost);
    }

    @PreDestroy
    public void methodPreDestroy() {
        System.out.println("Under Pre Destroy");
    }

    public LifecycleDependency getLifecycleDependency() {
        return this.lifecycleDependency;
    }

    public Boolean getPostCBoolean() {
        return this.isPostConstruct;
    }
}