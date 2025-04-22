package com.lioness.urlcompressor.statistics;

import com.lioness.urlcompressor.security.AuthorizationService;
import com.lioness.urlcompressor.url.LinkRepository;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class) // ✅ Enable Mockito annotations for unit tests
class StatisticsServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private StatisticsService statisticsService;

    /**
     * ✅ Smoke test: ensure method doesn't throw any exceptions with minimal setup.
     * We're not mocking any response intentionally — just testing base behavior.
     */
    @Test
    void testGetAllUserUrls_shouldNotThrow() {
        UrlRequest request = new UrlRequest();
        request.setAuthHeader("Bearer test-token");

        assertDoesNotThrow(() -> statisticsService.getAllUserUrls(request));
    }

    /**
     * 🧪 Test: getShortUrlClicks — if the URL does not exist, should return NOT_FOUND
     */
    @Test
    void testGetShortUrlClicks_urlNotFound_shouldReturnError() {
        // 🔐 Simulate authorized user
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("nonexistent-short-link");
        request.setAuthHeader("Bearer some-valid-token");

        UserEntity user = new UserEntity();
        when(authorizationService.getAuthorizedUser("Bearer some-valid-token"))
                .thenReturn(Optional.of(user));

        // ⛔ Simulate missing URL in database
        when(linkRepository.findByShortenedUrl("nonexistent-short-link"))
                .thenReturn(Optional.empty());

        // 💥 Call the method and verify the error response
        StatisticsResponse response = statisticsService.getShortUrlClicks(request);

        assertNotNull(response);
        assertEquals("URL не знайдено", response.getInfoMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
}

