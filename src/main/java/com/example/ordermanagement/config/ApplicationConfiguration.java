package com.example.ordermanagement.config;

import com.example.ordermanagement.common.generator.OrderReferanceGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ApplicationConfiguration {
    @Bean
    @Primary
    public OrderReferanceGenerator standardReferanceGenerator() {
        return new OrderReferanceGenerator();
    }

    @Bean
    public OrderReferanceGenerator priorityReferanceGenerator() {
        return new OrderReferanceGenerator();
    }
    // By this method my OrderReferanceGenerator class became spring managed bean
    /*
     * When our springboot application boots up, it scans the packages and find @Configuration annotation on ApplicationCOnfiguration
     * It executes the orderReferanceGenerator() method
     * It takes the registered java method and register and manage the SpringBean inside the application context. 
    */
}