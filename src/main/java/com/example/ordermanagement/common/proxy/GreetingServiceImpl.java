package com.example.ordermanagement.common.proxy;

import com.example.ordermanagement.common.proxy.GreetingService;
import org.springframework.stereotype.Service;

@Service("greetingService")
public class GreetingServiceImpl implements GreetingService {

    public GreetingServiceImpl() {
        System.out.println("GreetingServiceImpl constructor");
    }

    @Override 
    public void greet() {
        System.out.println("Greeting service implemented");
    }
}