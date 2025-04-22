package com.lioness.urlcompressor.logging;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserLog is a JPA entity used for storing logs of user authentication-related actions.
 * Each record tracks the username, type of action (login/logout/refresh), and timestamp.
 */
@Entity
@Table(name = "user_logs", schema = "link_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLog {

    // Primary key (auto-incremented)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The username associated with the logged action
    private String username;

    // Type of action performed by the user: login, logout, or token refresh
    private String action;

    // The exact date and time when the action occurred
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
