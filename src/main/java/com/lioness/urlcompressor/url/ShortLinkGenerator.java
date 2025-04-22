package com.lioness.urlcompressor.url;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * ShortLinkGenerator is a utility service responsible for generating
 * random short URL strings, used in the URL shortening service.
 */
@Service
public class ShortLinkGenerator {

    // Characters used in the short link: lowercase, uppercase, and digits
    private static final char[] SYMBOLS = (
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    ).toCharArray();

    // Minimum and maximum length for the short link (e.g., abc123 or AbCdef78)
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 8;

    /**
     * Generates a random short link.
     * The link consists of alphanumeric characters and is prefixed with "https://".
     *
     * @return a unique-looking short URL string
     */
    public String createShortLink() {
        Random random = new Random();
        int length = random.nextInt(MAX_LENGTH - MIN_LENGTH + 1) + MIN_LENGTH;

        StringBuilder shortUrl = new StringBuilder();
        for (int i = 0; i < length; i++) {
            shortUrl.append(SYMBOLS[random.nextInt(SYMBOLS.length)]);
        }

        return "https://" + shortUrl;
    }
}
