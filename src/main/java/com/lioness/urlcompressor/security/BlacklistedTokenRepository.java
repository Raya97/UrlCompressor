package com.lioness.urlcompressor.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * BlacklistedTokenRepository provides access to blacklisted JWT tokens.
 * It supports checking if a token is blacklisted and deleting expired entries.
 */
@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * Checks if a given token already exists in the blacklist.
     *
     * @param token the token to check
     * @return true if the token is blacklisted, false otherwise
     */
    boolean existsByToken(String token);

    /**
     * Deletes all blacklisted tokens that have expired before the specified time.
     * Useful for scheduled cleanup of old tokens.
     *
     * @param time the cutoff time for expiration
     */
    void deleteAllByExpiresAtBefore(LocalDateTime time);

    /**
     * Custom query method for checking if a token is blacklisted using JPQL.
     * Redundant with existsByToken(), but can be used for demonstration or optimization.
     *
     * @param token the token to check
     * @return true if the token is blacklisted, false otherwise
     */
    @Query("SELECT COUNT(bt) > 0 FROM BlacklistedToken bt WHERE bt.token = :token")
    boolean isTokenBlacklisted(@Param("token") String token);
}
