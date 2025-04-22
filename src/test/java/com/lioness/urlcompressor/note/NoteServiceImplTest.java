package com.lioness.urlcompressor.note;

import com.lioness.urlcompressor.notes.Note;
import com.lioness.urlcompressor.notes.NoteRepository;
import com.lioness.urlcompressor.notes.NoteService;
import com.lioness.urlcompressor.notes.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the NoteServiceImpl class using a mocked NoteRepository.
 */
class NoteServiceImplTest {

    private NoteRepository noteRepository; //  Mocked NoteRepository
    private NoteService noteService;       //  Real service implementation

    /**
     * Sets up the test environment before each test by initializing the mock and injecting it.
     */
    @BeforeEach
    void setUp() {
        noteRepository = Mockito.mock(NoteRepository.class); //  Manually create a mock
        noteService = new NoteServiceImpl(noteRepository);   //  Inject it into the service
    }

    /**
     * Test that verifies saving a note returns the expected result.
     */
    @Test
    @DisplayName("Save note — success")
    void testCreateNote_success() {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Test");

        when(noteRepository.save(note)).thenReturn(note); //  Mocking repository response

        Note result = noteService.save(note);

        assertNotNull(result); //  Ensures the returned object is not null
        assertEquals("Test", result.getTitle()); //  Verifies title match
    }

    /**
     * Test that verifies behavior when trying to fetch a non-existent note.
     */
    @Test
    @DisplayName("Find note by ID — not found returns null")
    void testGetNoteById_notFound() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty()); // 🧪 Simulate missing entity

        Note result = noteService.findById(1L);

        assertNull(result); //  Expecting null when note is not found
    }
}
