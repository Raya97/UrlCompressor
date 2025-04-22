package com.lioness.urlcompressor.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.security.TokenBlacklistService;
import com.lioness.urlcompressor.logging.UserLogService;

/**
 * ✅ Unit test for the UserController — testing the endpoint /api/v1/user/test
 */
@WebMvcTest(UserController.class) // Bootstraps ONLY UserController with minimal web context
@AutoConfigureMockMvc // Enables MockMvc
@Import(UserControllerTest.MockConfig.class) // Injects required mocked beans
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 🧪 Static test configuration for injecting mocked beans required by security or logging
     */
    @TestConfiguration
    static class MockConfig {
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean
        public TokenBlacklistService tokenBlacklistService() {
            return Mockito.mock(TokenBlacklistService.class);
        }

        @Bean
        public UserLogService userLogService() {
            return Mockito.mock(UserLogService.class);
        }
    }

    /**
     * ✅ Test: When a user with role USER accesses /api/v1/user/test, they should get a greeting
     */
    @Test
    @DisplayName("GET /api/v1/user/test — success")
    @WithMockUser(username = "lioness", roles = {"USER"}) // Mocks authentication context
    void userGreeting_success() throws Exception {
        mockMvc.perform(get("/api/v1/user/test"))
                .andExpect(status().isOk()) // 200 OK expected
                .andExpect(jsonPath("$.message").value("Привіт, lioness")) // JSON contains greeting
                .andExpect(jsonPath("$.role").value("[ROLE_USER]")); // JSON contains role
    }
}