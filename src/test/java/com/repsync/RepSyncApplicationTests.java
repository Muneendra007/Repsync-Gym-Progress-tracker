package com.repsync;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Basic Spring Boot context test.
 * Verifies that all Beans (Controllers, Services, JPA Repositories, and SecurityConfig) load correctly.
 */
@SpringBootTest
class RepSyncApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("=== RepSync Spring Boot Application Context Loaded Successfully ===");
    }
}
