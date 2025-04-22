package com.lioness.urlcompressor.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * NoteRequestDTO is a data transfer object used for creating or updating notes.
 * It includes validation constraints to ensure title and content meet requirements.
 */
@Data
public class NoteRequestDTO {

    // Title must not be blank and must be 255 characters or less
    @NotBlank(message = "Title must not be blank")
    @Size(max = 255, message = "Maximum title length is 255 characters")
    private String title;

    // Content is optional but limited to 5000 characters if provided
    @Size(max = 5000, message = "Content is too long (max 5000 characters)")
    private String content;
}
