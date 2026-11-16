package com.example.ordermanagement.common.scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
class ScopeExperimentServiceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScopeExperimentService scopeExperimentService;

    @Test
    void runScopeExperiments() {
        // 1. Retrieve SingletonScope twice from ApplicationContext -> Same object
        SingletonScope singleton1 = applicationContext.getBean(SingletonScope.class);
        SingletonScope singleton2 = applicationContext.getBean(SingletonScope.class);
        assertSame(singleton1, singleton2, "Singleton beans from context must be the exact same instance");

        // 2. Retrieve PrototypeScope twice from ApplicationContext -> Different objects
        PrototypeScope prototype1 = applicationContext.getBean(PrototypeScope.class);
        PrototypeScope prototype2 = applicationContext.getBean(PrototypeScope.class);
        assertNotSame(prototype1, prototype2, "Prototype beans from context must be completely unique instances");

        // 3. Retrieve ScopeExperimentService twice -> Same object
        ScopeExperimentService service1 = applicationContext.getBean(ScopeExperimentService.class);
        ScopeExperimentService service2 = applicationContext.getBean(ScopeExperimentService.class);
        assertSame(service1, service2, "ScopeExperimentService is a singleton and must return the same instance");

        // 4. Call the service's prototype-related method twice -> Same injected prototype object
        // We verify this by executing the method and checking that the frozen UUID remains unchanged.
        String firstCallUuid = scopeExperimentService.getPrototypeIdentifier();
        String secondCallUuid = scopeExperimentService.getPrototypeIdentifier();
        assertSame(firstCallUuid, secondCallUuid, "The prototype instance inside the singleton service is frozen and shared");
    }
}
