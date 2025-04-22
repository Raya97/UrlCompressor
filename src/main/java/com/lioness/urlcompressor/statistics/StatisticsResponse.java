package com.lioness.urlcompressor.statistics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * StatisticsResponse is a data transfer object (DTO) used to return
 * statistical data about shortened URLs in a structured API response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON output
@Data
public class StatisticsResponse {

    // Total number of clicks across all user's shortened URLs
    private Long totalClicks;

    // List of shortened URLs with their individual statistics
    private List<StatsUrlDto> urlList;

    // Optional info message for user feedback or error reporting
    private String infoMessage;

    // Internal HTTP status (not serialized in the JSON response)
    @JsonIgnore
    private HttpStatus status;

    /**
     * Constructs a success response with total clicks and URL data.
     *
     * @param totalClicks the sum of all clicks
     * @param urlList     the list of individual URL statistics
     * @param status      the HTTP status (usually 200 OK)
     */
    public StatisticsResponse(Long totalClicks, List<StatsUrlDto> urlList, HttpStatus status) {
        this.totalClicks = totalClicks;
        this.urlList = urlList;
        this.status = status;
    }

    /**
     * Constructs a failure or informational response with a message.
     *
     * @param infoMessage the message to be shown to the client
     * @param status      the HTTP status (e.g., 404, 400)
     */
    public StatisticsResponse(String infoMessage, HttpStatus status) {
        this.infoMessage = infoMessage;
        this.status = status;
    }

    /**
     * Static factory for building a successful response.
     *
     * @param totalClicks the total click count
     * @param urlList     the list of URLs with stats
     * @return a success StatisticsResponse with HTTP 200 OK
     */
    public static StatisticsResponse success(long totalClicks, List<StatsUrlDto> urlList) {
        return new StatisticsResponse(totalClicks, urlList, HttpStatus.OK);
    }

    /**
     * Static factory for building a failed response.
     *
     * @param message the error message
     * @param status  the HTTP status code
     * @return a failure StatisticsResponse with no data
     */
    public static StatisticsResponse failed(String message, HttpStatus status) {
        return new StatisticsResponse(message, status);
    }
}
