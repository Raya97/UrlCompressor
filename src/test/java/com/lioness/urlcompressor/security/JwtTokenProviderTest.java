package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Initialize and inject a dummy secret key via reflection
        jwtTokenProvider = new JwtTokenProvider();
        String secret = "TGlvbmVzc19LZXlfUmFpc2FfMjAyNV84ODAwMzUwOThfNjM3NTc1NzI3XzI4MDQxOTk3Xw==";
        ReflectionTestUtils.setField(jwtTokenProvider, "base64Secret", secret);
    }

    @Test
    void generateToken_andValidate_success() {
        // Generate an access token and validate it
        String token = jwtTokenProvider.generateToken("lioness", Role.USER);

        assertNotNull(token, "Generated token should not be null");
        assertTrue(jwtTokenProvider.validateToken(token), "Token should be valid");

        // Extract and verify username and role from token
        assertEquals("lioness", jwtTokenProvider.extractUsernameFromToken(token));
        assertEquals(Role.USER, jwtTokenProvider.extractRoleFromToken(token));
    }

    @Test
    void generateRefreshToken_andBlacklistTTL() {
        // Generate a refresh token and check TTL (should be ~7 days)
        String refreshToken = jwtTokenProvider.generateRefreshToken("lioness", Role.ADMIN);

        assertNotNull(refreshToken, "Refresh token should not be null");
        Duration ttl = jwtTokenProvider.getTokenTTL(refreshToken);

        assertTrue(ttl.toHours() >= 6, "Refresh token TTL should be at least 6 hours");
    }

    @Test
    void extractTokenFromHeader_shouldReturnOnlyToken() {
        // Extract raw token from Bearer header
        String header = "Bearer test.token.value";
        String token = jwtTokenProvider.extractTokenFromHeader(header);

        assertEquals("test.token.value", token, "Extracted token should match expected value");
    }

    @Test
    void extractTokenFromHeader_shouldReturnNullIfInvalid() {
        // Ensure extraction fails for null or malformed headers
        assertNull(jwtTokenProvider.extractTokenFromHeader(null), "Null header should return null");
        assertNull(jwtTokenProvider.extractTokenFromHeader("InvalidHeader"), "Malformed header should return null");
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        // Validate a token that is clearly not a JWT
        assertFalse(jwtTokenProvider.validateToken("this.is.not.jwt"), "Invalid JWT should return false");
    }
}
