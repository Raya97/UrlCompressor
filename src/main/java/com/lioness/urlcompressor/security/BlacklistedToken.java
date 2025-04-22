package com.lioness.urlcompressor.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * BlacklistedToken is a JPA entity representing JWT tokens that are no longer valid.
 * Typically used for logout or token rotation to prevent reuse of previously issued tokens.
 */
@Entity
@Table(name = "blacklisted_tokens", schema = "link_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    // Primary key (auto-incremented ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The exact token string that is blacklisted
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    // Expiration date/time of the token (used for cleanup/garbage collection)
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
