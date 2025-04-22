package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.user.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setup() {
        // Initialize mocks and inject them into the filter
        MockitoAnnotations.openMocks(this);
        jwtAuthFilter = new JwtAuthFilter(jwtTokenProvider);
    }

    @Test
    void shouldSetAuthentication_whenTokenIsValid() throws ServletException, IOException {
        // Arrange: mock a request with a valid Bearer token
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid.token.here");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Stub the token provider behavior
        when(jwtTokenProvider.extractTokenFromHeader("Bearer valid.token.here"))
                .thenReturn("valid.token.here");

        when(jwtTokenProvider.validateToken("valid.token.here")).thenReturn(true);
        when(jwtTokenProvider.extractUsernameFromToken("valid.token.here")).thenReturn("lioness");
        when(jwtTokenProvider.extractRoleFromToken("valid.token.here")).thenReturn(Role.ADMIN);

        // Act: apply the filter
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert: authentication should be set in the security context
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be set");
        assertEquals("lioness", SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

        // Ensure the request continues down the filter chain
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotSetAuthentication_whenTokenIsInvalid() throws ServletException, IOException {
        // Arrange: request with an invalid token
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Stub: token is invalid
        when(jwtTokenProvider.validateToken("invalid.token")).thenReturn(false);

        // Act: apply the filter
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Assert: authentication should NOT be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // Ensure the filter chain still proceeds
        verify(filterChain).doFilter(request, response);
    }
}
