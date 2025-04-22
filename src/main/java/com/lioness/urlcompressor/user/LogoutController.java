package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * LogoutController handles user logout requests by blacklisting the provided refresh token.
 * Once blacklisted, the token cannot be used to refresh authentication anymore.
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class LogoutController {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Handles logout by blacklisting the given refresh token.
     *
     * @param body a JSON body containing the refresh token
     * @return response indicating success or failure of the logout operation
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> body) {
        String token = body.get("refreshToken");

        // Validate input
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("message", "Refresh token is missing")
            );
        }

        try {
            // Calculate remaining TTL and add token to blacklist
            Duration ttl = jwtTokenProvider.getTokenTTL(token);
            tokenBlacklistService.blacklist(token, ttl);

            return ResponseEntity.ok(
                    Collections.singletonMap("message", "Logout successful. Token blacklisted.")
            );
        } catch (Exception e) {
            e.printStackTrace(); // Replace with logger in production
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("message", "Failed to log out: " + e.getMessage())
            );
        }
    }
}
