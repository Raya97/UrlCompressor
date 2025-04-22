package com.lioness.urlcompressor.user;


import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.security.TokenBlacklistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc(addFilters = false) // ✅ Disable security filters to test controller in isolation
@WebMvcTest(LogoutController.class)
@Import(LogoutControllerTest.MockBeans.class) // ✅ Import mocked dependencies
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * 🧪 Test config: Provide mocked dependencies
     */
    @TestConfiguration
    static class MockBeans {
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean
        public TokenBlacklistService tokenBlacklistService() {
            return Mockito.mock(TokenBlacklistService.class);
        }
    }

    /**
     * ✅ Test: Valid refresh token should be blacklisted successfully
     */
    @Test
    @DisplayName("✅ POST /logout with valid token returns 200")
    void testLogoutSuccess() throws Exception {
        String token = "valid.token.here";

        // Mock TTL (token expiration) retrieval
        Mockito.when(jwtTokenProvider.getTokenTTL(token))
                .thenReturn(Duration.ofDays(7));

        // Mock blacklisting (no exception thrown)
        Mockito.doNothing().when(tokenBlacklistService)
                .blacklist(Mockito.eq(token), Mockito.any());

        mockMvc.perform(post("/api/v1/user/logout")
                        .with(csrf()) // Required for POST request under Spring Security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + token + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful. Token blacklisted."));
    }

    /**
     * ❌ Test: Missing refreshToken in body should return 400 Bad Request
     */
    @Test
    @DisplayName("❌ POST /logout with missing token returns 400")
    void testLogoutMissingToken() throws Exception {
        mockMvc.perform(post("/api/v1/user/logout")
                        .with(csrf()) // Still required even for bad requests
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // No refreshToken field
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Refresh token is missing"));
    }
}
