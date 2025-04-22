package com.lioness.urlcompressor.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // Full Spring context – testing the actual SecurityConfig
@AutoConfigureMockMvc // Enables MockMvc for endpoint testing
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * ✅ Ensures the injected password encoder is of type BCryptPasswordEncoder
     */
    @Test
    @DisplayName("passwordEncoder should be instance of BCrypt")
    void passwordEncoder_shouldBeBCrypt() {
        assertNotNull(passwordEncoder, "PasswordEncoder should be injected");
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    /**
     * ✅ Registration with valid data should return 200 OK
     */
    @Test
    @DisplayName("Valid signup request should return 200")
    void signup_withValidData_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"user_" + System.currentTimeMillis() + "\", \"password\": \"securePass123\"}"))
                .andExpect(status().isOk());
    }

    /**
     * 🚫 Login field cannot be blank – should fail validation with 400
     */
    @Test
    @DisplayName("Validation fails when login is blank")
    void shouldFailValidation_whenLoginBlank() throws Exception {
        mockMvc.perform(post("/api/v1/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \" \", \"password\": \"securePassword\"}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * ✅ Admin role should access /admin/**
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN should access /admin/**")
    void adminAccess_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isOk());
    }

    /**
     * 🚫 USER should not have access to /admin/**
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER should not access /admin/**")
    void userAccessToAdmin_shouldFail() throws Exception {
        mockMvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isForbidden());
    }

    /**
     * ✅ MODERATOR should access /moderator/**
     */
    @Test
    @WithMockUser(roles = "MODERATOR")
    @DisplayName("MODERATOR should access /moderator/**")
    void moderatorAccess_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/moderator/test"))
                .andExpect(status().isOk());
    }

    /**
     * ✅ USER should access /user/**
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER should access /user/**")
    void userAccess_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/v1/user/test"))
                .andExpect(status().isOk());
    }

    /**
     * 🚫 Anonymous users should be blocked from /user/**
     */
    @Test
    @DisplayName("Unauthorized user should not access /user")
    void anonymousUserCannotAccessUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/user/test"))
                .andExpect(status().isUnauthorized());
    }
}
