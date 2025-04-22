package com.lioness.urlcompressor.notes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NoteServiceImpl is the implementation of the NoteService interface.
 * It handles business logic for creating, retrieving, updating, and deleting notes.
 */
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    // Injected repository to perform database operations
    private final NoteRepository noteRepository;

    /**
     * Deletes a note by its ID.
     *
     * @param id the ID of the note to delete
     */
    @Override
    public void delete(Long id) {
        noteRepository.deleteById(id);
    }

    /**
     * Saves a new or existing note.
     *
     * @param note the note to be saved
     * @return the saved note
     */
    @Override
    public Note save(Note note) {
        return noteRepository.save(note);
    }

    /**
     * Finds a note by its ID.
     *
     * @param id the ID of the note
     * @return the note if found, otherwise null
     */
    @Override
    public Note findById(Long id) {
        return noteRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves all notes owned by a specific user.
     *
     * @param owner the username of the note's owner
     * @return a list of notes belonging to the user
     */
    @Override
    public List<Note> findAllByOwner(String owner) {
        return noteRepository.findAllByOwner(owner);
    }
}
