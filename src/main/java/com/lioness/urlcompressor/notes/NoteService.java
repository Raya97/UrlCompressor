package com.lioness.urlcompressor.notes;

import java.util.List;

/**
 * NoteService defines the contract for note-related business logic.
 * It provides methods to create, retrieve, update, and delete notes.
 */
public interface NoteService {

    /**
     * Deletes a note by its ID.
     *
     * @param id the ID of the note to delete
     */
    void delete(Long id);

    /**
     * Finds a note by its ID.
     *
     * @param id the ID of the note
     * @return the note if found, or null if not found
     */
    Note findById(Long id);

    /**
     * Saves a new or existing note.
     *
     * @param note the note to be saved
     * @return the saved note instance (with ID if newly created)
     */
    Note save(Note note);

    /**
     * Retrieves all notes owned by a specific user.
     *
     * @param owner the username of the owner
     * @return a list of notes belonging to the user
     */
    List<Note> findAllByOwner(String owner);
}
