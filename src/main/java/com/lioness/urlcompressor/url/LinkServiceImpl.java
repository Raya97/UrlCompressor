package com.lioness.urlcompressor.url;

import com.lioness.urlcompressor.exceptions.UnauthorizedAccessException;
import com.lioness.urlcompressor.exceptions.UrlNotFoundException;
import com.lioness.urlcompressor.security.AuthorizationService;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.url.dto.UrlResponse;
import com.lioness.urlcompressor.url.dto.UrlStatsResponse;
import com.lioness.urlcompressor.user.UserEntity;
import com.lioness.urlcompressor.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.lioness.urlcompressor.util.MessageUtility.*;

/**
 * LinkServiceImpl provides the core implementation for URL shortening,
 * redirection, statistics, update, and deletion logic.
 */
@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final LongUrlValidator urlValidator;
    private final ShortLinkGenerator linkGenerator;
    private final AuthorizationService authorizationService;
    private final UserService userService;

    /**
     * Generates a new shortened URL.
     */
    @Override
    @Transactional
    public UrlResponse getShortUrlFromLongUrl(UrlRequest request) {
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(request.getAuthHeader());
        if (userOptional.isEmpty()) {
            return UrlResponse.failed(UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }

        String longUrl = request.getOriginalUrl();
        if (!urlValidator.isValid(longUrl)) {
            return UrlResponse.failed(INVALID_URL, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime expiresAt = request.getExpirationTime();
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return UrlResponse.failed(EXPIRED_URL, HttpStatus.BAD_REQUEST);
        }

        String shortUrl = linkGenerator.createShortLink();
        UrlEntity url = UrlEntity.builder()
                .shortenedUrl(shortUrl)
                .originalUrl(longUrl)
                .expiresOn(expiresAt)
                .owner(userOptional.get())
                .build();

        linkRepository.save(url);

        return UrlResponse.success(
                shortUrl,
                longUrl,
                LocalDateTime.now(),
                expiresAt,
                userOptional.get().getLogin(),
                URL_CREATED,
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves all shortened links for an authenticated user.
     */
    @Override
    public List<UrlResponse> getAllLinksForUser(String authHeader) {
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(authHeader);
        if (userOptional.isEmpty()) {
            return List.of(); // or throw UnauthorizedAccessException
        }

        UserEntity user = userOptional.get();
        return linkRepository.findByOwner(user).stream()
                .map(link -> UrlResponse.success(
                        "https://" + link.getShortenedUrl(),
                        link.getOriginalUrl(),
                        link.getCreatedOn(),
                        link.getExpiresOn(),
                        user.getLogin(),
                        "Link found",
                        HttpStatus.OK
                ))
                .toList();
    }

    /**
     * Expands a short URL to the original long URL.
     */
    @Override
    @Transactional
    public UrlResponse getLongUrlFromShortUrl(UrlRequest request) {
        String shortUrl = request.getOriginalUrl();
        if (shortUrl == null || shortUrl.isEmpty()) {
            return UrlResponse.failed(INVALID_URL, HttpStatus.BAD_REQUEST);
        }

        Optional<UrlEntity> urlOptional = linkRepository.findByShortenedUrl(shortUrl);
        if (urlOptional.isEmpty()) {
            return UrlResponse.failed(URL_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        UrlEntity url = urlOptional.get();
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(request.getAuthHeader());
        if (userOptional.isEmpty()) {
            return UrlResponse.failed(UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }

        if (!url.getOwner().equals(userOptional.get())) {
            return UrlResponse.failed("Access denied to this link", HttpStatus.FORBIDDEN);
        }

        if (url.getExpiresOn() != null && url.getExpiresOn().isBefore(LocalDateTime.now())) {
            return UrlResponse.failed(EXPIRED_URL, HttpStatus.GONE);
        }

        url.incrementClickCount();
        linkRepository.save(url);

        return UrlResponse.success(
                url.getShortenedUrl(),
                url.getOriginalUrl(),
                url.getCreatedOn(),
                url.getExpiresOn(),
                url.getOwner().getLogin(),
                URL_UPDATED,
                HttpStatus.OK
        );
    }

    /**
     * Updates an existing shortened URL's expiration date.
     */
    @Override
    @Transactional
    public UrlResponse updateUrl(UrlRequest request) {
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(request.getAuthHeader());
        if (userOptional.isEmpty()) {
            return UrlResponse.failed(UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }

        Optional<UrlEntity> urlOptional = linkRepository.findByShortenedUrl(request.getOriginalUrl());
        if (urlOptional.isEmpty()) {
            return UrlResponse.failed(URL_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        UrlEntity url = urlOptional.get();
        if (!url.getOwner().getId().equals(userOptional.get().getId())) {
            return UrlResponse.failed(UNAUTHORIZED_ACCESS, HttpStatus.FORBIDDEN);
        }

        url.setExpiresOn(request.getExpirationTime());
        linkRepository.save(url);

        return UrlResponse.success(
                url.getShortenedUrl(),
                url.getOriginalUrl(),
                url.getCreatedOn(),
                url.getExpiresOn(),
                userOptional.get().getLogin(),
                URL_UPDATED,
                HttpStatus.OK
        );
    }

    /**
     * Deletes a shortened URL.
     */
    @Override
    @Transactional
    public UrlResponse deleteUrl(UrlRequest request) {
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(request.getAuthHeader());
        if (userOptional.isEmpty()) {
            return UrlResponse.failed(UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }

        Optional<UrlEntity> urlOptional = linkRepository.findByShortenedUrl(request.getOriginalUrl());
        if (urlOptional.isEmpty()) {
            return UrlResponse.failed(URL_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        UrlEntity url = urlOptional.get();
        if (!url.getOwner().getId().equals(userOptional.get().getId())) {
            return UrlResponse.failed(UNAUTHORIZED_ACCESS, HttpStatus.FORBIDDEN);
        }

        linkRepository.delete(url);
        return UrlResponse.success(null, null, null, null, null, URL_DELETED, HttpStatus.OK);
    }

    /**
     * Retrieves click statistics for a short URL.
     */
    @Override
    public UrlStatsResponse getStatsByShortUrl(String shortUrl, String authHeader) {
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(authHeader);
        if (userOptional.isEmpty()) {
            throw new UnauthorizedAccessException("Unauthorized access to statistics.");
        }

        Optional<UrlEntity> urlOptional = linkRepository.findByShortenedUrl(shortUrl);
        if (urlOptional.isEmpty()) {
            throw new UrlNotFoundException("URL not found.");
        }

        UrlEntity url = urlOptional.get();
        if (!url.getOwner().getId().equals(userOptional.get().getId())) {
            throw new UnauthorizedAccessException("Access to another user's stats is forbidden.");
        }

        return new UrlStatsResponse(
                url.getShortenedUrl(),
                url.getOriginalUrl(),
                url.getCreatedOn(),
                url.getExpiresOn(),
                url.getClickCount()
        );
    }

    /**
     * Filters user's links by type (active/expired).
     */
    @Override
    public List<UrlResponse> getLinksByFilter(String type, String authHeader) {
        Optional<UserEntity> userOptional = authorizationService.getAuthorizedUser(authHeader);
        if (userOptional.isEmpty()) {
            return List.of(); // or throw UnauthorizedAccessException
        }

        UserEntity user = userOptional.get();
        List<UrlEntity> allLinks = linkRepository.findByOwner(user);

        LocalDateTime now = LocalDateTime.now();
        List<UrlEntity> filteredLinks = switch (type.toLowerCase()) {
            case "expired" -> allLinks.stream()
                    .filter(link -> link.getExpiresOn() != null && link.getExpiresOn().isBefore(now))
                    .toList();
            case "active" -> allLinks.stream()
                    .filter(link -> link.getExpiresOn() == null || link.getExpiresOn().isAfter(now))
                    .toList();
            default -> allLinks;
        };

        return filteredLinks.stream()
                .map(link -> UrlResponse.success(
                        "https://" + link.getShortenedUrl(),
                        link.getOriginalUrl(),
                        link.getCreatedOn(),
                        link.getExpiresOn(),
                        user.getLogin(),
                        "Link found",
                        HttpStatus.OK
                ))
                .toList();
    }
}
