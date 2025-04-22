package com.lioness.urlcompressor.url;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.url.dto.UrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LinkController.class) // ✅ Web-layer only test for LinkController
@AutoConfigureMockMvc(addFilters = false) // 🚫 Disable security filters for clean testing
@Import(LinkControllerTest.LinkControllerTestConfig.class) // 🧪 Import mocked beans
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ✅ Test: POST /shorten should return a short URL on success
     */
    @Test
    @DisplayName("POST /api/v1/link/shorten - success")
    void createShortLink_success() throws Exception {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("https://lioness.codes");
        request.setAuthHeader("Bearer test-token");

        UrlResponse response = UrlResponse.success(
                "abc123",
                "https://lioness.codes",
                null, null,
                "lioness",
                "Created",
                org.springframework.http.HttpStatus.OK
        );

        Mockito.when(linkService.getShortUrlFromLongUrl(any(UrlRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/link/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortLink").value("abc123"))
                .andExpect(jsonPath("$.fullUrl").value("https://lioness.codes"));
    }

    /**
     * 🧪 Test: POST /expand should handle exception via global error handler
     */
    @Test
    @DisplayName("POST /api/v1/link/expand - not found handled globally")
    void getOriginalUrl_notFound() throws Exception {
        UrlRequest request = new UrlRequest();
        request.setOriginalUrl("badcode");
        request.setAuthHeader("Bearer test-token");

        Mockito.when(linkService.getLongUrlFromShortUrl(any(UrlRequest.class)))
                .thenThrow(new ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Short URL not found"
                ));

        mockMvc.perform(post("/api/v1/link/expand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()) // 🚨 Expected to be caught by GlobalErrorHandler
                .andExpect(content().string(containsString("Сталася непередбачена помилка")))
                .andExpect(content().string(containsString("Short URL not found")));
    }

    /**
     * 🛠️ Mock configuration for LinkService & JwtTokenProvider
     */
    @TestConfiguration
    static class LinkControllerTestConfig {

        @Bean
        @Primary
        public LinkService linkService() {
            return Mockito.mock(LinkService.class);
        }

        @Bean
        @Primary
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }
    }
}

