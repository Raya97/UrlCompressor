package com.lioness.urlcompressor.notes;

import com.lioness.urlcompressor.notes.dto.NoteRequestDTO;
import com.lioness.urlcompressor.notes.dto.NoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * NoteController exposes REST endpoints for managing user-created notes.
 * All operations are protected — only the owner of the note can access or modify it.
 */
@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Validated
public class NoteController {

    private final NoteService noteService;

    /**
     * Retrieves a single note by ID, but only if it belongs to the current authenticated user.
     *
     * @param id        the note ID
     * @param principal the current authenticated user
     * @return the note if found and owned by the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse<Note>> getNote(@PathVariable Long id, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Note note = noteService.findById(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }

        if (!note.getOwner().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return ResponseEntity.ok(NoteResponse.success(note, "Note found", HttpStatus.OK));
    }

    /**
     * Creates a new note and assigns it to the current authenticated user.
     *
     * @param dto       the note data (title, content)
     * @param principal the current authenticated user
     * @return the newly created note
     */
    @PostMapping
    public ResponseEntity<NoteResponse<Note>> createNote(@Valid @RequestBody NoteRequestDTO dto, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setOwner(principal.getName());

        Note saved = noteService.save(note);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NoteResponse.success(saved, "Note created", HttpStatus.CREATED));
    }

    /**
     * Returns all notes owned by the current authenticated user.
     *
     * @param principal the current authenticated user
     * @return list of notes owned by the user
     */
    @GetMapping
    public ResponseEntity<NoteResponse<List<Note>>> getAllNotes(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        List<Note> notes = noteService.findAllByOwner(principal.getName());
        return ResponseEntity.ok(NoteResponse.success(notes, "Note list", HttpStatus.OK));
    }

    /**
     * Updates an existing note if it belongs to the current user.
     *
     * @param id        the note ID
     * @param dto       the updated title and content
     * @param principal the current authenticated user
     * @return the updated note
     */
    @PatchMapping("/{id}")
    public ResponseEntity<NoteResponse<Note>> updateNote(@PathVariable Long id,
                                                         @Valid @RequestBody NoteRequestDTO dto,
                                                         Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Note note = noteService.findById(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }

        if (!note.getOwner().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());

        Note updated = noteService.save(note);
        return ResponseEntity.ok(NoteResponse.success(updated, "Note updated", HttpStatus.OK));
    }

    /**
     * Deletes a note if it exists and belongs to the current user.
     *
     * @param id        the note ID
     * @param principal the current authenticated user
     * @return success message with 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<NoteResponse<Void>> deleteNote(@PathVariable Long id, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Note note = noteService.findById(id);
        if (note == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found");
        }

        if (!note.getOwner().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        noteService.delete(id);
        return ResponseEntity.ok(NoteResponse.success(null, "Note deleted", HttpStatus.OK));
    }
}
