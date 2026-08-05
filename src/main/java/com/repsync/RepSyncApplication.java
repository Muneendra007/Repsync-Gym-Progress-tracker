package com.repsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * RepSync Spring Boot Web Application Entry Point.
 * 
 * Launches the embedded Tomcat web server on port 8080
 * and exposes RESTful JSON APIs for the React frontend.
 * 
 * Note: To run the desktop Java Swing UI instead, execute com.repsync.Main.
 */
@SpringBootApplication(scanBasePackages = {
    "com.repsync.config",
    "com.repsync.controller",
    "com.repsync.service",
    "com.repsync.security"
})
@EntityScan("com.repsync.model")
@EnableJpaRepositories("com.repsync.repository")
public class RepSyncApplication {

    public static void main(String[] args) {
        System.out.println("=== Starting RepSync Spring Boot Web API Server on Port 8080 ===");
        com.repsync.database.SchemaInitializer.initialize();
        SpringApplication.run(RepSyncApplication.class, args);
    }
}
