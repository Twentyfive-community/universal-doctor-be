package com.example.chatbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatBeApplication {

    public static void main(String[] args) {
        // Start the Spring Boot application
        SpringApplication.run(ChatBeApplication.class, args);
        System.out.println("Token generation service is running on http://localhost:8080/token/generate");
    }

}
