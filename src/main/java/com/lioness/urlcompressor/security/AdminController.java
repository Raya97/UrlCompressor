package com.lioness.urlcompressor.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController exposes admin-only endpoints, such as checking admin access
 * and viewing all blacklisted JWT tokens.
 *
 * All endpoints should be protected with ADMIN role-based access.
 */
@RestController
@RequestMapping("/api/v1/admin")
// @PreAuthorize("hasRole('ADMIN')") // Uncomment when method-level security is enabled
public class AdminController {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructor injection of required dependencies.
     *
     * @param repo the repository for managing blacklisted tokens
     * @param tokenProvider the JWT utility class
     */
    public AdminController(BlacklistedTokenRepository repo, JwtTokenProvider tokenProvider) {
        this.blacklistedTokenRepository = repo;
        this.jwtTokenProvider = tokenProvider;
    }

    /**
     * Test endpoint to verify that admin access is working.
     *
     * @param authentication the current authenticated user
     * @return a greeting string if the user has admin rights
     */
    @GetMapping("/test")
    public String adminGreeting(Authentication authentication) {
        return "Hello, admin " + authentication.getName();
    }

    /**
     * Retrieves a list of all blacklisted JWT tokens.
     * Should only be accessible to users with the ADMIN role.
     *
     * @return list of blacklisted tokens
     */
    @GetMapping("/blacklisted-tokens")
    public ResponseEntity<List<BlacklistedToken>> getAllBlacklistedTokens() {
        List<BlacklistedToken> tokens = blacklistedTokenRepository.findAll();
        return ResponseEntity.ok(tokens);
    }
}
