package com.lioness.urlcompressor.statistics;

import com.lioness.urlcompressor.security.AuthorizationService;
import com.lioness.urlcompressor.url.LinkRepository;
import com.lioness.urlcompressor.url.UrlEntity;
import com.lioness.urlcompressor.user.UserEntity;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * StatisticsService handles the business logic related to URL statistics,
 * including total clicks, active links, and individual short URL analytics.
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final LinkRepository linkRepository;
    private final AuthorizationService authorizationService;

    /**
     * Returns all shortened URLs belonging to the authenticated user,
     * along with the total number of clicks.
     *
     * @param request the request containing the Authorization header
     * @return a response with URL statistics or an error message
     */
    @Transactional
    public StatisticsResponse getAllUserUrls(UrlRequest request) {
        Optional<UserEntity> userOpt = authorizationService.getAuthorizedUser(request.getAuthHeader());

        if (userOpt.isEmpty()) {
            return StatisticsResponse.failed("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        List<UrlEntity> urls = linkRepository.findByOwner(userOpt.get());

        if (urls.isEmpty()) {
            return StatisticsResponse.failed("No URLs found", HttpStatus.NOT_FOUND);
        }

        List<StatsUrlDto> list = convertUrlsToDto(urls);
        long totalClicks = list.stream().map(StatsUrlDto::getClicks).reduce(0L, Long::sum);
        return StatisticsResponse.success(totalClicks, list);
    }

    /**
     * Returns only active (non-expired) shortened URLs for the user,
     * along with the total click count.
     *
     * @param request the request with authorization info
     * @return a list of active URLs or an error response
     */
    @Transactional
    public StatisticsResponse getActiveUserUrls(UrlRequest request) {
        Optional<UserEntity> userOpt = authorizationService.getAuthorizedUser(request.getAuthHeader());

        if (userOpt.isEmpty()) {
            return StatisticsResponse.failed("Authorization failed", HttpStatus.UNAUTHORIZED);
        }

        List<UrlEntity> urls = linkRepository.findByOwner(userOpt.get());

        if (urls.isEmpty()) {
            return StatisticsResponse.failed("No active URLs found", HttpStatus.NOT_FOUND);
        }

        List<StatsUrlDto> activeUrls = filterActiveUrls(urls);
        long totalClicks = activeUrls.stream().map(StatsUrlDto::getClicks).reduce(0L, Long::sum);
        return StatisticsResponse.success(totalClicks, activeUrls);
    }

    /**
     * Returns the click count for a specific shortened URL.
     *
     * @param request contains the short URL and Authorization token
     * @return total click count or error message
     */
    @Transactional
    public StatisticsResponse getShortUrlClicks(UrlRequest request) {
        Optional<UserEntity> userOpt = authorizationService.getAuthorizedUser(request.getAuthHeader());

        if (userOpt.isEmpty()) {
            return StatisticsResponse.failed("Invalid or missing token", HttpStatus.UNAUTHORIZED);
        }

        Optional<UrlEntity> urlOpt = linkRepository.findByShortenedUrl(request.getOriginalUrl());

        if (urlOpt.isEmpty()) {
            return StatisticsResponse.failed("Shortened URL not found", HttpStatus.NOT_FOUND);
        }

        UrlEntity url = urlOpt.get();
        return StatisticsResponse.success(url.getClickCount(), null);
    }

    /**
     * Converts a list of UrlEntity objects to StatsUrlDto with click stats and timestamps.
     *
     * @param urls list of URL entities
     * @return list of DTOs with statistics
     */
    private List<StatsUrlDto> convertUrlsToDto(List<UrlEntity> urls) {
        List<StatsUrlDto> list = new ArrayList<>();
        for (UrlEntity url : urls) {
            list.add(new StatsUrlDto(
                    url.getShortenedUrl(),
                    url.getOriginalUrl(),
                    url.getClickCount(),
                    url.getExpiresOn() == null || url.getExpiresOn().isAfter(LocalDateTime.now()),
                    url.getCreatedOn(),
                    url.getExpiresOn()
            ));
        }
        return list;
    }

    /**
     * Filters only active (not expired) URLs from the list.
     *
     * @param urls all URLs owned by the user
     * @return a filtered list of only active URLs
     */
    private List<StatsUrlDto> filterActiveUrls(List<UrlEntity> urls) {
        List<StatsUrlDto> list = new ArrayList<>();
        for (UrlEntity url : urls) {
            if (url.getExpiresOn() == null || url.getExpiresOn().isAfter(LocalDateTime.now())) {
                list.add(new StatsUrlDto(
                        url.getShortenedUrl(),
                        url.getOriginalUrl(),
                        url.getClickCount(),
                        true,
                        url.getCreatedOn(),
                        url.getExpiresOn()
                ));
            }
        }
        return list;
    }
}
