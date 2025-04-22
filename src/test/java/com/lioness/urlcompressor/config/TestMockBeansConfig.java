package com.lioness.urlcompressor.config;

import com.lioness.urlcompressor.security.JwtAuthFilter;
import com.lioness.urlcompressor.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * TestMockBeansConfig provides mock implementations for beans used in tests.
 * This setup allows you to test secured controllers without requiring actual JWT parsing.
 */
@TestConfiguration
public class TestMockBeansConfig {

    /**
     * Mocks the JwtTokenProvider bean to always return valid data during tests.
     *
     * @return a mocked JwtTokenProvider
     */
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        JwtTokenProvider mock = Mockito.mock(JwtTokenProvider.class);

        // Always treat tokens as valid
        when(mock.validateToken(anyString())).thenReturn(true);

        // Always extract "lioness" as the username
        when(mock.extractUsernameFromToken(anyString())).thenReturn("lioness");

        return mock;
    }

    /**
     * Overrides JwtAuthFilter to inject authentication manually during tests.
     *
     * @param jwtTokenProvider the mocked JwtTokenProvider
     * @return a custom JwtAuthFilter that short-circuits filtering
     */
    @Bean
    public JwtAuthFilter jwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthFilter(jwtTokenProvider) {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                // 👇 Manually set authentication for test context
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken("lioness", null, Collections.emptyList())
                );
                return true; // ✅ Skip filter logic afterward
            }
        };
    }
}
