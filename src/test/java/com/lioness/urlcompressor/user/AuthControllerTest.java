package com.lioness.urlcompressor.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lioness.urlcompressor.logging.UserLogService;
import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.security.TokenBlacklistService;
import com.lioness.urlcompressor.user.dto.UserRequestDTO;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // ✅ ДОДАНО

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.MockDependencies.class)
@AutoConfigureMockMvc(addFilters = false) // 🔕 Disable security filters for unit test simplicity
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private UserLogService userLogService;

    /**
     * 🧪 Provides mocked dependencies required for AuthController
     */
    @TestConfiguration
    static class MockDependencies {
        @Bean public UserServiceImpl userService() {
            return Mockito.mock(UserServiceImpl.class);
        }

        @Bean public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean public TokenBlacklistService tokenBlacklistService() {
            return Mockito.mock(TokenBlacklistService.class);
        }

        @Bean public UserLogService userLogService() {
            return Mockito.mock(UserLogService.class);
        }
    }

    /**
     * 🛑 Test: unknown user tries to sign in
     */
    @Test
    @DisplayName("POST /api/v1/user/signin - unknown user (not found)")
    void signin_invalidLogin_shouldReturnUnauthorized() throws Exception {
        UserRequestDTO credentials = new UserRequestDTO("ghost", "password123");

        Mockito.when(userService.findByLogin("ghost"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/user/signin")
                        .with(csrf()) // ✅ Required for security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Помилка авторизації: Користувача не знайдено"));
    }

    /**
     * 🛑 Test: existing user, but wrong password
     */
    @Test
    @DisplayName("POST /api/v1/user/signin - incorrect password")
    void signin_invalidPassword_shouldReturnUnauthorized() throws Exception {
        UserRequestDTO credentials = new UserRequestDTO("lioness", "wrong-password");

        UserEntity user = UserEntity.builder()
                .login("lioness")
                .passwordHash("encoded-correct-password")
                .build();

        Mockito.when(userService.findByLogin("lioness"))
                .thenReturn(Optional.of(user));

        Mockito.when(userService.passwordMatches("wrong-password", "encoded-correct-password"))
                .thenReturn(false);

        mockMvc.perform(post("/api/v1/user/signin")
                        .with(csrf()) // ✅ Required
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message")
                        .value("Помилка авторизації: Невірний пароль"));
    }

    /**
     * ✅ Test: successful login — returns tokens
     */
    @Test
    @DisplayName("POST /api/v1/user/signin - successful login")
    void signin_success_shouldReturnTokens() throws Exception {
        UserRequestDTO credentials = new UserRequestDTO("lioness", "password123");

        UserEntity user = UserEntity.builder()
                .login("lioness")
                .passwordHash("encoded-password")
                .build();

        Mockito.when(userService.findByLogin("lioness"))
                .thenReturn(Optional.of(user));
        Mockito.when(userService.passwordMatches("password123", "encoded-password"))
                .thenReturn(true);
        Mockito.when(jwtTokenProvider.generateToken(eq("lioness"), any()))
                .thenReturn("mock-access-token");
        Mockito.when(jwtTokenProvider.generateRefreshToken(eq("lioness"), any()))
                .thenReturn("mock-refresh-token");

        mockMvc.perform(post("/api/v1/user/signin")
                        .with(csrf()) // ✅ CSRF token still needed in test context
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"))
                .andExpect(jsonPath("$.message").value("Авторизація успішна"));
    }
}
