package com.example.ordermanagement.common.proxy;

import com.example.ordermanagement.common.proxy.GreetingService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class GreetingServiceProxyPostProcessor implements BeanPostProcessor {

    private static final String TARGET_BEAN_NAME = "greetingService";

    @Override 
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanException {
        return bean;
    }

    @Override 
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
        if (TARGET_BEAN_NAME.equalsIgnoreCase(beanName)) {
            System.out.println("[BPP] Intercepting '" + beanName + "' after initialization. Swapping for a JDK Dynamic Proxy.");

            // Create the invokation handler to intercept calls
            InvokationHandler handler = new InvokationHandler() {
                @Override 
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // log before executing business logic
                    System.out.println("[JDK Proxy] >>> BEFORE executing method: " + method.getName());
                    return method.invoke(bean, args);
                }
            };
        }

        // Generate and return the JDK dynamic Proxy instance
        return Proxy.newProxyInstance(
            bean.getClass().getClassLoader(), 
            bean.getClass().getInterfaces(), 
            handler
        );

        return bean;
    }

}