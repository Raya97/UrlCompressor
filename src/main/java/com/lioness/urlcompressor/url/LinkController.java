package com.lioness.urlcompressor.url;

import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.url.dto.UrlResponse;
import com.lioness.urlcompressor.url.dto.UrlStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LinkController handles all HTTP endpoints related to URL shortening,
 * expansion, modification, deletion, and analytics.
 */
@RestController
@RequestMapping("/api/v1/link/")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    /**
     * Generates a new shortened URL based on the provided original URL.
     *
     * @param request     contains the long/original URL
     * @param authHeader  the Authorization header (JWT token)
     * @return UrlResponse with the newly created short link
     */
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlRequest request,
                                                  @RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        request.setAuthHeader(authHeader);
        UrlResponse response = linkService.getShortUrlFromLongUrl(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    /**
     * Expands a shortened URL back to its original long form.
     *
     * @param request     contains the short link to decode
     * @param authHeader  the Authorization header
     * @return UrlResponse with the full original URL
     */
    @PostMapping("/expand")
    public ResponseEntity<UrlResponse> expandUrl(@RequestBody UrlRequest request,
                                                 @RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        request.setAuthHeader(authHeader);
        UrlResponse response = linkService.getLongUrlFromShortUrl(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    /**
     * Modifies an existing shortened URL (e.g., updates expiration date).
     *
     * @param request     contains the updated data
     * @param authHeader  the Authorization header
     * @return UrlResponse with the modified short link details
     */
    @PostMapping("/modify")
    public ResponseEntity<UrlResponse> modifyUrl(@RequestBody UrlRequest request,
                                                 @RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        request.setAuthHeader(authHeader);
        UrlResponse response = linkService.updateUrl(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    /**
     * Deletes a shortened URL from the database.
     *
     * @param request     contains the short link to remove
     * @param authHeader  the Authorization header
     * @return UrlResponse confirming deletion
     */
    @PostMapping("/remove")
    public ResponseEntity<UrlResponse> removeUrl(@RequestBody UrlRequest request,
                                                 @RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        request.setAuthHeader(authHeader);
        UrlResponse response = linkService.deleteUrl(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    /**
     * Retrieves all shortened URLs created by the authenticated user.
     *
     * @param authHeader the Authorization header
     * @return List of UrlResponse objects
     */
    @GetMapping("/all")
    public ResponseEntity<List<UrlResponse>> getAllLinks(@RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        List<UrlResponse> allLinks = linkService.getAllLinksForUser(authHeader);
        return ResponseEntity.ok(allLinks);
    }

    /**
     * Retrieves click statistics for a specific shortened URL.
     *
     * @param request     contains the short URL to analyze
     * @param authHeader  the Authorization header
     * @return UrlStatsResponse with click count and timestamps
     */
    @PostMapping("/stats")
    public ResponseEntity<UrlStatsResponse> getStats(@RequestBody UrlRequest request,
                                                     @RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        request.setAuthHeader(authHeader);
        UrlStatsResponse stats = linkService.getStatsByShortUrl(request.getOriginalUrl(), authHeader);
        return ResponseEntity.ok(stats);
    }

    /**
     * Filters the user's shortened URLs based on expiration status (active or expired).
     *
     * @param type        filter type ("active" or "expired")
     * @param authHeader  the Authorization header
     * @return List of filtered UrlResponse objects
     */
    @GetMapping("/filter")
    public ResponseEntity<List<UrlResponse>> filterLinks(@RequestParam(defaultValue = "active") String type,
                                                         @RequestHeader(value = "Authorization", defaultValue = "") String authHeader) {
        List<UrlResponse> filtered = linkService.getLinksByFilter(type, authHeader);
        return ResponseEntity.ok(filtered);
    }
}
