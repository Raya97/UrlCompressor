package com.lioness.urlcompressor.notes;

import jakarta.persistence.*;
import lombok.*;

/**
 * Note is a JPA entity representing a single user-created note.
 * Each note has a title, optional content, and is associated with an owner (username).
 */
@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    // Primary key (auto-generated ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The title of the note (required, validated in DTO)
    private String title;

    // The body/content of the note (optional, up to 5000 characters)
    private String content;

    // The username of the user who owns this note
    private String owner;
}
