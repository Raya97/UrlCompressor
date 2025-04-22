package com.lioness.urlcompressor.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * StatsUrlDto is a Data Transfer Object that holds statistics for a single shortened URL.
 * It includes information about the original URL, click count, status, and timestamps.
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null values from the JSON response
public class StatsUrlDto {

    // The shortened version of the URL (e.g., abc123)
    private String shortUrl;

    // The original (long) URL (e.g., https://example.com/some-page)
    private String longUrl;

    // Number of times this short URL has been clicked
    private long clicks;

    // Indicates whether the URL is still active (not expired)
    private boolean isActive;

    // When the short URL was created
    private LocalDateTime createdAt;

    // When the short URL will expire (nullable)
    private LocalDateTime expiresAt;
}
