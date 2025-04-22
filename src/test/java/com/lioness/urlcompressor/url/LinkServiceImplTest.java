package com.lioness.urlcompressor.url;

import com.lioness.urlcompressor.security.AuthorizationService;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.url.dto.UrlResponse;
import com.lioness.urlcompressor.user.UserEntity;
import com.lioness.urlcompressor.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 🧪 Use Mockito JUnit 5 extension
class LinkServiceImplTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LongUrlValidator urlValidator;

    @Mock
    private ShortLinkGenerator linkGenerator;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private LinkServiceImpl linkService; // 🎯 System under test

    /**
     * 🚫 Test: Unauthorized user should receive UNAUTHORIZED response
     */
    @Test
    void testGetShortUrlFromLongUrl_unauthorized() {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://lioness.codes");
        request.setAuthHeader("Bearer invalid-token");

        when(authorizationService.getAuthorizedUser("Bearer invalid-token")).thenReturn(Optional.empty());

        UrlResponse response = linkService.getShortUrlFromLongUrl(request);

        assertEquals("Будь ласка, увійдіть у систему для доступу до ваших URL-адрес.", response.getStatusMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
    }

    /**
     * ❌ Test: Invalid URL should return BAD_REQUEST
     */
    @Test
    void testGetShortUrlFromLongUrl_invalidUrl() {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("ftp://something.weird");
        request.setAuthHeader("Bearer valid-token");

        UserEntity user = new UserEntity();
        when(authorizationService.getAuthorizedUser("Bearer valid-token")).thenReturn(Optional.of(user));
        when(urlValidator.isValid("ftp://something.weird")).thenReturn(false); // 👎 Not http/https

        UrlResponse response = linkService.getShortUrlFromLongUrl(request);

        assertEquals("Вказана некоректна URL-адреса.", response.getStatusMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
    }

    /**
     * 🕰️ Test: Expired expiration date should return BAD_REQUEST
     */
    @Test
    void testGetShortUrlFromLongUrl_expiredDate() {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://lioness.codes");
        request.setExpirationTime(LocalDateTime.now().minusDays(1)); // 💀 Already expired
        request.setAuthHeader("Bearer token");

        UserEntity user = new UserEntity();
        when(authorizationService.getAuthorizedUser("Bearer token")).thenReturn(Optional.of(user));
        when(urlValidator.isValid("https://lioness.codes")).thenReturn(true); // ✅ URL is valid

        UrlResponse response = linkService.getShortUrlFromLongUrl(request);

        assertEquals("Термін дії URL-адреси закінчився.", response.getStatusMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
    }
}
