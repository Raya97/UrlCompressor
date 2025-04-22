package com.lioness.urlcompressor.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * Controller for API version 2.
 * Handles incoming requests intended for new or upcoming features under the /api/v2 path.
 */
@RestController
@RequestMapping("/api/v2/")
public class ApiVersionTwoController {

    /**
     * Catches all requests under /api/v2/** and responds with a development notice.
     * Useful placeholder until v2 endpoints are implemented.
     *
     * @return response message indicating v2 is in development
     */
    @RequestMapping("/**")
    public ResponseEntity<Map<String, String>> handleVersionTwoRequests() {
        return ResponseEntity.ok(Collections.singletonMap(
                "message", "Version 2 is currently under development."
        ));
    }
}
