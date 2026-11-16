package com.example.ordermanagement.common.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LifecycleProbeTest {

    @Autowired
    private LifecycleProbe lifecycleProbe;

    @Test
    void verifyLifecycleProbeMilestones() {
        // 1. Verify that the bean exists (Injected successfully)
        assertNotNull(lifecycleProbe, "LifecycleProbe bean should be registered and injected");

        // 2. Verify that its injected dependency is available inside the probe
        assertNotNull(lifecycleProbe.getLifecycleDependency(), "Injected LifecycleDependency should not be null");

        // 3. Verify that its initialization flag is true (Proves @PostConstruct executed)
        assertTrue(lifecycleProbe.getPostCBoolean(), "The initialization flag must be true after @PostConstruct runs");
    }
}
