package com.lioness.urlcompressor.url;

import org.springframework.stereotype.Component;

/**
 * LongUrlValidator is a simple utility component used to validate
 * the structure of a long/original URL before shortening it.
 */
@Component
public class LongUrlValidator {

    /**
     * Checks whether the provided URL is valid.
     * Currently, it only ensures the URL is not null and starts with "http".
     *
     * @param url the long/original URL to validate
     * @return true if the URL is considered valid, false otherwise
     */
    public boolean isValid(String url) {
        return url != null && url.startsWith("http");
    }
}
