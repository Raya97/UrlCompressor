package com.lioness.urlcompressor.url;

import com.lioness.urlcompressor.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * UrlEntity represents a shortened URL stored in the database.
 * Each instance contains information about the original URL, the short version,
 * the click count, timestamps, and the owning user.
 */
@Entity
@Table(schema = "link_manager", name = "short_links")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlEntity {

    /**
     * Primary key: Unique identifier of the short URL record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The original long-form URL that was shortened.
     */
    @Column(name = "original_url", length = 2000, nullable = false)
    private String originalUrl;

    /**
     * The unique shortened version of the URL (e.g., abc123).
     */
    @Column(name = "shortened_url", length = 50, nullable = false, unique = true)
    private String shortenedUrl;

    /**
     * Counter tracking how many times this short URL has been accessed.
     */
    @Column(name = "click_count", nullable = false)
    private long clickCount;

    /**
     * Timestamp indicating when this short URL was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    /**
     * Optional expiration date — the short URL becomes inactive after this time.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresOn;

    /**
     * The user who owns this short URL.
     * This defines a many-to-one relationship with the user entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity owner;

    /**
     * Checks if the link is still active (i.e., not expired).
     *
     * @return true if the link is active, false otherwise
     */
    public boolean isActive() {
        return this.expiresOn == null || this.expiresOn.isAfter(LocalDateTime.now());
    }

    /**
     * Increments the click counter by one.
     */
    public void incrementClickCount() {
        this.clickCount++;
    }
}
