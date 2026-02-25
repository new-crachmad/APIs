package com.eventmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.eventmaster")
public class EventMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventMasterApplication.class, args);
    }
}