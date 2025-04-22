package com.lioness.urlcompressor.error;

import com.lioness.urlcompressor.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for the ErrorTestController.
 * Ensures that the global exception handler catches runtime errors correctly.
 */
@WebMvcTest(controllers = ErrorTestController.class)
@AutoConfigureMockMvc(addFilters = false) // ✅ disables Spring Security filters for testing
@Import(ErrorTestControllerTest.MockConfig.class)
class ErrorTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests that a RuntimeException thrown in the controller
     * is handled and results in HTTP 500 Internal Server Error.
     */
    @Test
    void shouldReturnError() throws Exception {
        mockMvc.perform(get("/api/v1/error"))
                .andExpect(status().isInternalServerError()); // 💥 expects HTTP 500 from simulated error
    }

    /**
     * Mock configuration for dependencies not directly tested.
     * In this case, we mock JwtTokenProvider just to satisfy the context.
     */
    @TestConfiguration
    static class MockConfig {
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }
    }
}
