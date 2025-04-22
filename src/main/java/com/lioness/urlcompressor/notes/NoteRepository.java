package com.lioness.urlcompressor.notes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * NoteRepository provides CRUD operations for the Note entity.
 * It also includes a custom query method for fetching notes by owner (username).
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Retrieves all notes owned by the specified user.
     *
     * @param owner the username of the note owner
     * @return a list of notes belonging to the user
     */
    List<Note> findAllByOwner(String owner);
}
