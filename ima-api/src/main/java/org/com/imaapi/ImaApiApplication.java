package org.com.imaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class ImaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImaApiApplication.class, args);
    }
}