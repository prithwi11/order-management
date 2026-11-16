package com.example.ordermanagement.common.service;

import com.example.ordermanagement.common.generator.OrderReferanceGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

@SpringBootTest
class ReferenceTestServiceTest {

    @Autowired
    private ReferanceTestService referenceTestService;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldGenerateReference() {
        String reference = referenceTestService.generateTest();
        System.out.println("Generated reference: " + reference);

        assertNotNull(reference);
        assertTrue(reference.startsWith("ORD"));
    }

    @Test
    void shouldVerifySingletonScopeBehaviour() {
        OrderReferanceGenerator first = applicationContext.getBean(OrderReferanceGenerator.class);
        OrderReferanceGenerator second = applicationContext.getBean(OrderReferanceGenerator.class);

        assertSame(first, second);
    }

    @Test
    void shouldInspectRegisteredGeneratorBeans() {
        Map<String, OrderReferanceGenerator> generators = applicationContext.getBeansOfType(OrderReferanceGenerator.class);

        generators.forEach((name, bean) -> {
            System.out.println("Bean name : " + name);
            System.out.println("Bean class : " + bean.getClass().getName());
        });

        assertEquals(2, generators.size());

        // getBean() retrieves a bean by type
        // getBean("standardReferanceGenerator") retrieves a bean by name
        // getBeansOfType(OrderReferanceGenerator.class) retrieves all matching beans.
    }
}
