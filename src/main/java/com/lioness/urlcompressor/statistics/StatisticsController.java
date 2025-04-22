package com.lioness.urlcompressor.statistics;

import com.lioness.urlcompressor.url.dto.UrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * StatisticsController exposes endpoints for retrieving statistics related to shortened URLs.
 * It allows authenticated users to get total clicks, active links, and all their URLs.
 */
@RestController
@RequestMapping("/api/v1/statistics/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Retrieves a list of all shortened URLs owned by the authenticated user.
     *
     * @param header the Authorization header containing the JWT token
     * @return a response containing all user URLs and related metadata
     */
    @GetMapping("/all")
    public ResponseEntity<StatisticsResponse> getUserUrls(
            @RequestHeader(value = "Authorization", defaultValue = "") String header) {
        UrlRequest request = new UrlRequest();
        request.setAuthHeader(header);
        StatisticsResponse response = statisticsService.getAllUserUrls(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Retrieves only active (non-expired) shortened URLs owned by the user.
     *
     * @param header the Authorization header containing the JWT token
     * @return a response containing a list of active URLs
     */
    @GetMapping("/active")
    public ResponseEntity<StatisticsResponse> getActiveUserUrls(
            @RequestHeader(value = "Authorization", defaultValue = "") String header) {
        UrlRequest request = new UrlRequest();
        request.setAuthHeader(header);
        StatisticsResponse response = statisticsService.getActiveUserUrls(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Retrieves the number of times a specific shortened URL has been accessed.
     *
     * @param request contains the short URL
     * @param header  the Authorization header containing the JWT token
     * @return a response with the click count for the given short URL
     */
    @GetMapping("/clicks")
    public ResponseEntity<StatisticsResponse> getClicksByShortUrl(
            @RequestBody UrlRequest request,
            @RequestHeader(value = "Authorization", defaultValue = "") String header) {
        request.setAuthHeader(header);
        StatisticsResponse response = statisticsService.getShortUrlClicks(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
