package com.lioness.urlcompressor.error;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ErrorTestController provides a test endpoint that intentionally throws an exception.
 * It's used to verify that the global exception handling mechanism is working correctly.
 */
@RestController
@RequestMapping("/api/v1/error")
public class ErrorTestController {

    /**
     * Test endpoint to simulate a server-side error.
     * This method always throws a RuntimeException to trigger the global error handler.
     *
     * @return never returns a value; always throws an exception
     */
    @GetMapping
    public String errorEndpoint() {
        // 💥 Intentionally throw RuntimeException to test the global error handler
        throw new RuntimeException("Simulated internal error");
    }
}
