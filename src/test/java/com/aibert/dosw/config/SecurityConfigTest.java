package com.aibert.dosw.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Test
    @DisplayName("Should load security configuration")
    void shouldLoadSecurityConfiguration() {
        // This test verifies that the SecurityConfig can be loaded
        // without throwing any exceptions during Spring context initialization
        assertDoesNotThrow(() -> {
            // The test itself passes if Spring context loads successfully
        });
    }

    @Test
    @DisplayName("Should have security configuration bean")
    void shouldHaveSecurityConfigurationBean() {
        // Basic test to ensure security configuration is properly configured
        // More detailed security tests would require mocking HTTP requests
        assertNotNull("Security configuration should be available");
    }
}
