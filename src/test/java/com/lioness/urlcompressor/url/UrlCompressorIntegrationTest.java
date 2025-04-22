package com.lioness.urlcompressor.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.user.Role;
import com.lioness.urlcompressor.user.UserEntity;
import com.lioness.urlcompressor.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yaml")
@Import(UrlCompressorIntegrationTest.TestSecurityConfig.class)
@WithMockUser(username = "testuser", roles = {"USER"}) // ✅ Mock user with USER role
class UrlCompressorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    /**
     * ✅ Smoke test — just makes sure Spring context loads without exploding.
     */
    @Test
    void contextLoads() {
        assertNotNull(mockMvc);
    }

    /**
     * 🧪 Full integration test — creates a user, generates JWT, shortens a URL.
     */
    @Test
    void createAndRetrieveLink() throws Exception {
        // 🧑‍💻 Create dummy user in test DB
        UserEntity user = new UserEntity();
        user.setLogin("testuser2");
        user.setPasswordHash("doesNotMatterForTest");
        user.setRole(Role.USER);
        userRepository.save(user); // 🔐 must be saved to link URLs

        // 🛡️ Generate JWT manually
        String jwt = jwtTokenProvider.generateToken("testuser", Role.USER);

        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://lioness.codes");

        String jsonRequest = objectMapper.writeValueAsString(request); // 🔧 serialize to JSON

        // 🚀 Perform POST request to /shorten endpoint
        mockMvc.perform(post("/api/v1/link/shorten")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    /**
     * 🔧 Test security config — disables CSRF and allows all requests (for testing only!)
     */
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable()) // 😈 no CSRF in test mode
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // ✅ allow everything
                    .build();
        }
    }
}
