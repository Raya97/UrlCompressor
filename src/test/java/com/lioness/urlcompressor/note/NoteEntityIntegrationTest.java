package com.lioness.urlcompressor.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lioness.urlcompressor.config.TestMockBeansConfig;
import com.lioness.urlcompressor.notes.Note;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the Note entity using a real PostgreSQL database via Testcontainers.
 * JWT validation is mocked to isolate test logic.
 */
@SpringBootTest //  Full Spring context
@AutoConfigureMockMvc //  Enable MockMvc
@Testcontainers //  Enable Testcontainers support
@Transactional //  Rollback DB changes after each test
@Import(TestMockBeansConfig.class) // Inject mock JwtTokenProvider
class NoteEntityIntegrationTest {

    // 🔌 Testcontainer for PostgreSQL 15
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("postgres");

    // 🔧 Dynamically set Spring datasource properties from the container
    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Integration test that sends a POST request to create a note,
     * and verifies that the response contains correct data.
     */
    @Test
    @DisplayName("Create and verify Note entity in PostgreSQL Testcontainer with mocked JWT")
    void createNote_andVerify() throws Exception {
        Note note = new Note();
        note.setTitle("Integration Note");
        note.setContent("This is a note created during integration test.");

        String jwt = "Bearer mocked-token"; // Token doesn't matter here due to mocked provider

        mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", jwt)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Integration Note"))
                .andExpect(jsonPath("$.data.content").value("This is a note created during integration test."));
    }
}
