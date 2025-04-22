package com.lioness.urlcompressor.url;

import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.url.dto.UrlResponse;
import com.lioness.urlcompressor.url.dto.UrlStatsResponse;

import java.util.List;

/**
 * LinkService defines the contract for handling all business logic
 * related to URL shortening, expansion, management, and analytics.
 */
public interface LinkService {

    /**
     * Retrieves all shortened URLs created by a specific user.
     *
     * @param username the username or Authorization header (depending on usage)
     * @return a list of UrlResponse objects
     */
    List<UrlResponse> getAllLinksForUser(String username);

    /**
     * Creates a new shortened URL based on the provided request.
     *
     * @param request the request containing original URL and expiration time
     * @return UrlResponse containing the generated short URL and metadata
     */
    UrlResponse getShortUrlFromLongUrl(UrlRequest request);

    /**
     * Expands a given short URL to its full original URL.
     *
     * @param request the request containing the short URL
     * @return UrlResponse with the full URL
     */
    UrlResponse getLongUrlFromShortUrl(UrlRequest request);

    /**
     * Updates an existing shortened URL (e.g., changes expiration time).
     *
     * @param request contains updated URL details
     * @return UrlResponse with updated URL info
     */
    UrlResponse updateUrl(UrlRequest request);

    /**
     * Deletes a shortened URL based on the provided request.
     *
     * @param request contains the short URL to delete
     * @return UrlResponse indicating success or failure
     */
    UrlResponse deleteUrl(UrlRequest request);

    /**
     * Retrieves statistics for a specific short URL, such as click count and timestamps.
     *
     * @param shortUrl   the short link
     * @param authHeader the Authorization token
     * @return UrlStatsResponse containing detailed statistics
     */
    UrlStatsResponse getStatsByShortUrl(String shortUrl, String authHeader);

    /**
     * Filters user's URLs based on type ("active", "expired", etc.).
     *
     * @param type        the filter type
     * @param authHeader  the Authorization token
     * @return filtered list of UrlResponse objects
     */
    List<UrlResponse> getLinksByFilter(String type, String authHeader);
}
