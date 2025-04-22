package com.lioness.urlcompressor.statistics;

import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.url.dto.UrlRequest;
import com.lioness.urlcompressor.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // ❌ Security filters disabled for simplicity
@Import(StatisticsControllerTest.StatisticsTestConfig.class) // ✅ Import test config with mocks
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * ✅ Test: GET /api/v1/statistics/all should return valid response
     * Simulates a successful scenario for retrieving all user URLs with stats.
     */
    @Test
    @DisplayName("GET /api/v1/statistics/all - success")
    void getAllUserUrls_success() throws Exception {
        // 🔧 Mock JWT behavior
        Mockito.when(jwtTokenProvider.extractUsernameFromToken(anyString()))
                .thenReturn("mockedUser");
        Mockito.when(jwtTokenProvider.extractRoleFromToken(anyString()))
                .thenReturn(Role.USER);
        Mockito.when(jwtTokenProvider.validateToken(anyString()))
                .thenReturn(true);

        // 🧪 Prepare mock response from statisticsService
        StatsUrlDto dto = new StatsUrlDto(
                "abc123",
                "https://lioness.codes",
                10L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)
        );
        StatisticsResponse response = StatisticsResponse.success(10L, List.of(dto));
        Mockito.when(statisticsService.getAllUserUrls(any(UrlRequest.class))).thenReturn(response);

        // 🚀 Perform the GET request
        mockMvc.perform(get("/api/v1/statistics/all")
                        .header("Authorization", "Bearer mock.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClicks").value(10))
                .andExpect(jsonPath("$.urlList[0].shortUrl").value("abc123"))
                .andExpect(jsonPath("$.urlList[0].clicks").value(10));
    }

    /**
     * 🛠️ Provides mocked dependencies for StatisticsController
     */
    @TestConfiguration
    static class StatisticsTestConfig {

        @Bean
        @Primary
        public JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(JwtTokenProvider.class);
        }

        @Bean
        @Primary
        public StatisticsService statisticsService() {
            return Mockito.mock(StatisticsService.class);
        }
    }
}

