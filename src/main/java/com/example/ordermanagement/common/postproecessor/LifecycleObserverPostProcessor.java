package com.example.ordermanagement.common.postproecessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LifecycleObserverPostProcessor implements BeanPostProcessor {

    private static final String TARGET_BEAN_NAME = "lifecycleProbe";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Filter logs to only observe the target bean
        if (TARGET_BEAN_NAME.equalsIgnoreCase(beanName)) {
            System.out.println(">>> Bean: " + beanName + " | Callback: postProcessBeforeInitialization (Runs BEFORE @PostConstruct)");
        }
        return bean; // Must return the bean instance (or a wrapper/proxy)
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Filter logs to only observe the target bean
        if (TARGET_BEAN_NAME.equalsIgnoreCase(beanName)) {
            System.out.println(">>> Bean: " + beanName + " | Callback: postProcessAfterInitialization (Runs AFTER @PostConstruct)");
        }
        return bean; // Must return the bean instance (or a wrapper/proxy)
    }
}
