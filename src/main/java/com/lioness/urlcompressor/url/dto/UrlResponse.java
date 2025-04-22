package com.lioness.urlcompressor.url.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * UrlResponse is a Data Transfer Object used to return information
 * about the result of a URL shortening operation.
 * Includes both success and error response formats.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from the JSON response
public class UrlResponse {

    /**
     * The generated short URL.
     */
    private String shortLink;

    /**
     * The original long URL that was shortened.
     */
    private String fullUrl;

    /**
     * Timestamp indicating when the short URL was created.
     */
    private LocalDateTime createdOn;

    /**
     * Optional expiration time for the short URL.
     */
    private LocalDateTime expiresOn;

    /**
     * The username of the user who created the short link.
     */
    private String createdBy;

    /**
     * Message describing the result of the operation.
     */
    private String statusMessage;

    /**
     * Flag indicating whether the operation was successful.
     */
    private boolean success;

    /**
     * HTTP status associated with the response.
     * This field is ignored in the JSON response.
     */
    @JsonIgnore
    private HttpStatus httpStatus;

    /**
     * Constructor for creating a success response.
     *
     * @param shortLink      the shortened URL
     * @param fullUrl        the original full URL
     * @param createdOn      creation timestamp
     * @param expiresOn      optional expiration timestamp
     * @param createdBy      the user who created the URL
     * @param statusMessage  informational message
     * @param httpStatus     HTTP status to return
     */
    public UrlResponse(String shortLink,
                       String fullUrl,
                       LocalDateTime createdOn,
                       LocalDateTime expiresOn,
                       String createdBy,
                       String statusMessage,
                       HttpStatus httpStatus) {
        this.shortLink = shortLink;
        this.fullUrl = fullUrl;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
        this.createdBy = createdBy;
        this.statusMessage = statusMessage;
        this.httpStatus = httpStatus;
        this.success = true;
    }

    /**
     * Constructor for creating a failed response.
     *
     * @param statusMessage  error message
     * @param httpStatus     HTTP status to return
     */
    public UrlResponse(String statusMessage, HttpStatus httpStatus) {
        this.statusMessage = statusMessage;
        this.httpStatus = httpStatus;
        this.success = false;
    }

    /**
     * Static factory method for building a successful response.
     */
    public static UrlResponse success(String shortLink,
                                      String fullUrl,
                                      LocalDateTime createdOn,
                                      LocalDateTime expiresOn,
                                      String createdBy,
                                      String statusMessage,
                                      HttpStatus httpStatus) {
        return new UrlResponse(shortLink, fullUrl, createdOn, expiresOn, createdBy, statusMessage, httpStatus);
    }

    /**
     * Static factory method for building a failed response.
     */
    public static UrlResponse failed(String statusMessage, HttpStatus httpStatus) {
        return new UrlResponse(statusMessage, httpStatus);
    }

    /**
     * Getter for internal HTTP status.
     * Useful for setting response status in the controller.
     */
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
