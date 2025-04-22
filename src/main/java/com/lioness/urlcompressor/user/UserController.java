package com.lioness.urlcompressor.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController exposes basic endpoints accessible to authenticated users.
 * This controller is often used for testing or displaying user-related information.
 */
@RestController
@RequestMapping("/api/v1")
public class UserController {

    /**
     * Simple test endpoint to verify that the user is authenticated.
     * Returns the user's username and role(s).
     *
     * @param authentication Spring Security authentication object
     * @return a response with a greeting and the user's authorities
     */
    @GetMapping("/user/test")
    public ResponseEntity<Map<String, String>> userGreeting(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, " + authentication.getName());
        response.put("role", authentication.getAuthorities().toString());
        return ResponseEntity.ok(response);
    }
}
