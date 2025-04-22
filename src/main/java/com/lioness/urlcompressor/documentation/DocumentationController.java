package com.lioness.urlcompressor.documentation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;

/**
 * REST controller that serves OpenAPI documentation files from the classpath.
 * Supports JSON and YAML formats. Files must be located in the `resources/public.docs` folder.
 */
@RestController
@RequestMapping("/api")
public class DocumentationController {

    /**
     * Returns the stable version of the API documentation (usually the latest version).
     *
     * @return Contents of api_version_2.json
     */
    @GetMapping("/docs")
    public ResponseEntity<String> getStableApiDocs() throws IOException {
        return loadFile("public.docs/api_version_2.json");
    }

    /**
     * Returns the OpenAPI documentation for API version 1.
     *
     * @return Contents of openapi_structure.json (or openapiV1.json if exists)
     */
    @GetMapping({"/v1/docs", "/v1/docs/openapiV1.json"})
    public ResponseEntity<String> getV1ApiDocs() throws IOException {
        return loadFile("public.docs/openapi_structure.json");
    }

    /**
     * Returns the OpenAPI documentation for API version 2.
     *
     * @return Contents of api_version_2.json
     */
    @GetMapping({"/v2/docs", "/v2/docs/openapiV2.json"})
    public ResponseEntity<String> getV2ApiDocs() throws IOException {
        return loadFile("public.docs/api_version_2.json");
    }

    /**
     * Dynamic endpoint for accessing any documentation file from `public.docs`.
     * Accepts .json and .yaml/.yml files by name.
     *
     * Example: /api/docs/openapi_definition.yaml
     *
     * @param filename the name of the file to load (with extension)
     * @return ResponseEntity with file content and proper Content-Type
     */
    @GetMapping("/docs/{filename:.+}")
    public ResponseEntity<String> getAnyDocFile(@PathVariable String filename) throws IOException {
        return loadFile("public.docs/" + filename);
    }

    /**
     * Helper method that loads a file from the classpath and returns its content.
     * Automatically sets the appropriate Content-Type header based on file extension.
     *
     * @param path Path to the file inside the classpath (e.g. "public.docs/filename.json")
     * @return ResponseEntity containing the file contents
     */
    private ResponseEntity<String> loadFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Documentation file not found: " + path);
        }

        String content = Files.readString(resource.getFile().toPath());

        HttpHeaders headers = new HttpHeaders();
        if (path.endsWith(".yaml") || path.endsWith(".yml")) {
            headers.setContentType(MediaType.valueOf("application/yaml"));
        } else {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }
}
