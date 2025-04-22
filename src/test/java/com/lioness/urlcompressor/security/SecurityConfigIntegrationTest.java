package com.lioness.urlcompressor.security;
import static org.mockito.Mockito.mock;

import com.lioness.urlcompressor.notes.NoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test configuration to mock external dependencies like NoteService
     * to isolate security testing from business logic.
     */
    @TestConfiguration
    static class StubConfig {
        @Bean
        public NoteService noteService() {
            return mock(NoteService.class);
        }
    }

    /**
     * ✅ Admin user should have access to /admin endpoint
     */
    @Test
    @DisplayName("🔒 ADMIN має доступ до /admin")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isOk());
    }

    /**
     * 🚫 Regular USER should be forbidden from accessing /admin endpoint
     */
    @Test
    @DisplayName("🚫 USER не має доступу до /admin")
    @WithMockUser(username = "user", roles = {"USER"})
    void userCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/admin/test"))
                .andExpect(status().isForbidden());
    }

    /**
     * ✅ USER should be able to access /user endpoint
     */
    @Test
    @DisplayName("✅ USER має доступ до /user")
    @WithMockUser(username = "user", roles = {"USER"})
    void userCanAccessUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/user/test"))
                .andExpect(status().isOk());
    }

    /**
     * 🚫 Anonymous (unauthenticated) users should be blocked from /user
     */
    @Test
    @DisplayName("🚫 Неавторизований користувач не має доступу до /user")
    void anonymousUserCannotAccessUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/user/test"))
                .andExpect(status().isUnauthorized());
    }
}
