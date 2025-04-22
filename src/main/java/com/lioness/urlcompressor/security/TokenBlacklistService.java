package com.lioness.urlcompressor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * TokenBlacklistService provides functionality for blacklisting JWT tokens.
 * It allows tokens to be marked as invalid (e.g., after logout or token rotation),
 * and automatically cleans up expired entries on a schedule.
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Adds a token to the blacklist with a specified expiration duration (TTL).
     *
     * @param token the JWT token to blacklist
     * @param ttl   the time-to-live (duration) until the token should be considered expired
     */
    public void blacklist(String token, Duration ttl) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(ttl);

        System.out.println("📌 Adding token to blacklist: " + token);
        System.out.println("📅 Will expire at: " + expiresAt);

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expiresAt(expiresAt)
                .build();

        blacklistedTokenRepository.save(blacklistedToken);
        System.out.println("✅ Token successfully saved to database!");
    }

    /**
     * Checks whether a given token is present in the blacklist.
     *
     * @param token the token to check
     * @return true if the token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        System.out.println("🔎 Checking if token is blacklisted: " + token);
        return blacklistedTokenRepository.existsByToken(token);
    }

    /**
     * Scheduled task that runs hourly to delete expired tokens from the blacklist.
     * Helps keep the blacklist table clean and efficient.
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // every hour
    @Transactional
    public void cleanExpiredTokens() {
        blacklistedTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
