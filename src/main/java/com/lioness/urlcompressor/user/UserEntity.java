package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.url.UrlEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * UserEntity represents a registered user in the database.
 * Each user can own multiple shortened URLs (one-to-many relationship with {@link UrlEntity}).
 */
@Entity
@Table(name = "app_users", schema = "link_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    /**
     * Unique identifier for the user (primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The role of the user (e.g. USER, ADMIN, MODERATOR).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * User's login name (must be unique).
     */
    @Column(length = 50, nullable = false, unique = true)
    private String login;

    /**
     * The hashed password of the user.
     */
    @Column(name = "password_hash")
    private String passwordHash;

    /**
     * Set of shortened links owned by this user.
     * This establishes a one-to-many relationship with the UrlEntity.
     */
    @OneToMany(
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UrlEntity> links;
}
