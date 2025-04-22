package com.lioness.urlcompressor.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * ModeratorController provides endpoints accessible only to users with the MODERATOR role.
 * It is used to test or build functionality restricted to moderators.
 */
@RestController
@RequestMapping("/api/v1/moderator")
public class ModeratorController {

    /**
     * Test endpoint to verify MODERATOR role access.
     * Only accessible to authenticated users with role MODERATOR.
     *
     * @return confirmation message if access is granted
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<String> testModeratorAccess() {
        return ResponseEntity.ok("Access granted for MODERATOR");
    }
}
