package com.lioness.urlcompressor.v2;

import com.lioness.urlcompressor.security.JwtAuthFilter;
import com.lioness.urlcompressor.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ✅ Test class for API v2 controller — currently returns "in development" message.
 */
@WebMvcTest(ApiVersionTwoController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔕 disables security filters
@Import(ApiVersionTwoControllerTest.MockBeans.class)
class ApiVersionTwoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockBeans {

        // 🔧 Mocking JwtTokenProvider (not used directly here but required for context)
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        // 🔧 Mocking JwtAuthFilter to bypass security filtering in tests
        @Bean
        public JwtAuthFilter jwtAuthFilter() throws Exception {
            JwtAuthFilter mockFilter = Mockito.mock(JwtAuthFilter.class);

            Mockito.doAnswer(invocation -> {
                ServletRequest request = invocation.getArgument(0);
                ServletResponse response = invocation.getArgument(1);
                FilterChain chain = invocation.getArgument(2);
                chain.doFilter(request, response); // 👈 pass it through
                return null;
            }).when(mockFilter).doFilter(Mockito.any(), Mockito.any(), Mockito.any());

            return mockFilter;
        }
    }

    @Test
    @DisplayName("GET /api/v2/** — should return 200 OK with 'in development' message")
    void handleVersionTwoRequests_shouldReturnDevelopmentMessage() throws Exception {
        mockMvc.perform(get("/api/v2/some/endpoint"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("в розробці")));
    }
}
