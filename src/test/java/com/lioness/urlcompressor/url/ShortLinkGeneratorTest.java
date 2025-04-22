package com.lioness.urlcompressor.url;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ShortLinkGeneratorTest {

    private final ShortLinkGenerator generator = new ShortLinkGenerator();

    /**
     * ✅ All generated links must start with https:// — trust issues otherwise.
     */
    @Test
    void generatedLink_shouldStartWithHttps() {
        String link = generator.createShortLink();
        assertTrue(link.startsWith("https://"), "Link should start with https://");
    }

    /**
     * ✅ Short code length must be between 6 and 8 characters (just enough entropy).
     */
    @Test
    void generatedLink_shouldBeWithinExpectedLength() {
        String link = generator.createShortLink();
        String code = link.replace("https://", "");
        assertTrue(code.length() >= 6 && code.length() <= 8, "Length should be between 6 and 8");
    }

    /**
     * 🔁 Generate a lot of links and make sure there are no duplicates.
     * ⚠️ Not cryptographically safe, but good enough for a demo.
     */
    @Test
    void generatedLinks_shouldBeMostlyUnique() {
        Set<String> uniqueLinks = new HashSet<>();
        int count = 10_000;

        for (int i = 0; i < count; i++) {
            String link = generator.createShortLink();
            assertTrue(uniqueLinks.add(link), "Duplicate found: " + link);
        }

        assertEquals(count, uniqueLinks.size(), "Links should all be unique");
    }

    /**
     * 🎲 Run it 5 times to confirm consistency of format and length.
     */
    @RepeatedTest(5)
    void generateMultipleLinks_shouldAlwaysRespectLengthAndPrefix() {
        String link = generator.createShortLink();
        String code = link.replace("https://", "");

        assertTrue(link.startsWith("https://"), "Link must start with https://");
        assertTrue(code.length() >= 6 && code.length() <= 8, "Short code should be between 6 and 8 chars");
    }
}
