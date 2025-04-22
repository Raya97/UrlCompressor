package com.lioness.urlcompressor.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for AdminController with role-based access verification.
 */
@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = true) // ✅ Enable security filters
@Import(AdminControllerTest.MockedBeansConfig.class) // ✅ Inject mocked beans
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Configuration class that provides mocked beans for the test context.
     */
    @TestConfiguration
    static class MockedBeansConfig {

        @Bean
        public BlacklistedTokenRepository blacklistedTokenRepository() {
            return mock(BlacklistedTokenRepository.class);
        }

        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return mock(JwtTokenProvider.class);
        }

        @Bean
        public JwtAuthFilter jwtAuthFilter() {
            JwtAuthFilter mockFilter = mock(JwtAuthFilter.class);

            try {
                // 👇 Override the default filter behavior to inject a mocked SecurityContext
                doAnswer(invocation -> {
                    HttpServletRequest request = invocation.getArgument(0);
                    HttpServletResponse response = invocation.getArgument(1);
                    FilterChain chain = invocation.getArgument(2);

                    // 🔐 Simulate authentication with ROLE_ADMIN
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    "mockUser",
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                            )
                    );
                    SecurityContextHolder.setContext(context);

                    chain.doFilter(request, response); // Continue filter chain
                    return null;
                }).when(mockFilter).doFilter(any(), any(), any());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return mockFilter;
        }
    }

    /**
     * Test that verifies admin endpoint is accessible to users with ADMIN role.
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/test — access granted for ADMIN")
    void adminAccess_withAdminRole_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isOk());
    }

    /**
     * Test that verifies admin endpoint is forbidden for users with USER role.
     */
    @Test
    @WithMockUser(roles = {"USER"})
    @DisplayName("GET /api/v1/admin/test — access denied for USER")
    void adminAccess_withUserRole_shouldFail() throws Exception {
        mockMvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isForbidden());
    }
}
