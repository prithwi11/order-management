package com.example.ordermanagement.common.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class GreetingServiceProxyPostProcessor implements BeanPostProcessor {

    private static final String TARGET_BEAN_NAME = "greetingService";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (TARGET_BEAN_NAME.equalsIgnoreCase(beanName)) {
            System.out.println("[BPP] Intercepting '" + beanName + "' after initialization. Swapping for a JDK Dynamic Proxy.");

            // Create the invocation handler to intercept calls
            InvocationHandler handler = new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // Log before executing business logic
                    System.out.println("[JDK Proxy] >>> BEFORE executing method: " + method.getName());
                    return method.invoke(bean, args);
                }
            };

            // Generate and return the JDK dynamic Proxy instance inside the if-block
            return Proxy.newProxyInstance(
                    bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(),
                    handler
            );
        }

        return bean;
    }
}
