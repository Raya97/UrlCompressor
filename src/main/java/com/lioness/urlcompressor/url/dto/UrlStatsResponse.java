package com.lioness.urlcompressor.url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UrlStatsResponse is a DTO used to represent statistics for a specific shortened URL.
 * It includes the short and full URLs, creation and expiration timestamps, and click count.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlStatsResponse {

    /**
     * The generated short URL (e.g., abc123).
     */
    private String shortLink;

    /**
     * The original long URL before shortening.
     */
    private String fullUrl;

    /**
     * Timestamp indicating when the short URL was created.
     */
    private LocalDateTime createdOn;

    /**
     * Optional expiration timestamp after which the URL becomes inactive.
     */
    private LocalDateTime expiresOn;

    /**
     * The total number of times this short URL has been accessed.
     */
    private long clickCount;
}
