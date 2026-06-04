package com.phishguard.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhishGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhishGuardApplication.class, args);
    }
}
