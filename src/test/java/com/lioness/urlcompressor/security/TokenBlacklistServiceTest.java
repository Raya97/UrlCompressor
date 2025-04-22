package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.security.BlacklistedTokenRepository;
import com.lioness.urlcompressor.security.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Enables Mockito for this test class
class TokenBlacklistServiceTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    /**
     * ✅ Test case to check if a token is correctly identified as blacklisted
     */
    @Test
    void testIsTokenBlacklisted_true() {
        when(blacklistedTokenRepository.existsByToken("token123")).thenReturn(true);

        boolean result = tokenBlacklistService.isBlacklisted("token123");

        assertTrue(result, "Token should be detected as blacklisted");
    }

    /**
     * 🧹 Verifies that the clean-up method correctly triggers the repository deletion
     */
    @Test
    void cleanExpiredTokens_shouldCallRepository() {
        tokenBlacklistService.cleanExpiredTokens();
        verify(blacklistedTokenRepository).deleteAllByExpiresAtBefore(any());
    }

    /**
     * 🕒 Tests that blacklisted tokens are saved with the correct expiration time
     */
    @Test
    void testBlacklist_shouldSaveTokenWithCorrectExpiry() {
        String token = "some.token.here";
        Duration ttl = Duration.ofHours(1);

        tokenBlacklistService.blacklist(token, ttl);

        ArgumentCaptor<BlacklistedToken> captor = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistedTokenRepository).save(captor.capture());

        BlacklistedToken saved = captor.getValue();
        assertEquals(token, saved.getToken(), "Token should match");
        assertNotNull(saved.getExpiresAt(), "Expiration time should not be null");
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()), "Expiration should be in the future");
    }

    /**
     * 🚫 Test case for non-blacklisted token – should return false
     */
    @Test
    void testIsTokenBlacklisted_false() {
        when(blacklistedTokenRepository.existsByToken("token456")).thenReturn(false);

        boolean result = tokenBlacklistService.isBlacklisted("token456");

        assertFalse(result, "Token should not be blacklisted");
    }
}
