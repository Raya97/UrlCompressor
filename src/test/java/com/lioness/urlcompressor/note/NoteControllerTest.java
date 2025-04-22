package com.lioness.urlcompressor.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lioness.urlcompressor.notes.Note;
import com.lioness.urlcompressor.notes.NoteController;
import com.lioness.urlcompressor.notes.NoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the NoteController.
 * Covers successful creation and "not found" scenarios for notes.
 */
@AutoConfigureMockMvc(addFilters = false) // ✅ Disable Spring Security filters for isolated testing
@WebMvcTest(NoteController.class)
@Import(NoteControllerTest.MockConfig.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test for successfully creating a new note.
     * Expected result: HTTP 201 Created with correct title in response.
     */
    @Test
    @DisplayName("POST /api/v1/notes - success")
    void createNote_success() throws Exception {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test Note");
        note.setOwner("lioness");

        when(noteService.save(any(Note.class))).thenReturn(note);

        mockMvc.perform(post("/api/v1/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(note))
                        .principal(() -> "lioness"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Test Note"));
    }

    /**
     * Test when a note with a non-existent ID is requested.
     * Expected result: HTTP 404 Not Found with appropriate message.
     */
    @Test
    @DisplayName("GET /api/v1/notes/{id} - not found")
    void getNoteById_notFound() throws Exception {
        when(noteService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/notes/99")
                        .principal(() -> "lioness"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusMessage").value("Нотатку не знайдено"));
    }

    /**
     * Inject mock implementations of required dependencies for testing.
     */
    @TestConfiguration
    static class MockConfig {

        @Bean
        public NoteService noteService() {
            return Mockito.mock(NoteService.class);
        }

        @Bean
        public com.lioness.urlcompressor.security.JwtTokenProvider jwtTokenProvider() {
            return Mockito.mock(com.lioness.urlcompressor.security.JwtTokenProvider.class);
        }

        @Bean
        public com.lioness.urlcompressor.security.TokenBlacklistService tokenBlacklistService() {
            return Mockito.mock(com.lioness.urlcompressor.security.TokenBlacklistService.class);
        }

        @Bean
        public com.lioness.urlcompressor.security.JwtAuthFilter jwtAuthFilter() {
            return Mockito.mock(com.lioness.urlcompressor.security.JwtAuthFilter.class);
        }
    }
}
