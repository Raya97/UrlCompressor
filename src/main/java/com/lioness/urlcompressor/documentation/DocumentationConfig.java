package com.lioness.urlcompressor.documentation;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to expose static OpenAPI documentation files
 * located in the `public.docs` directory of the classpath.
 */
@Configuration
public class DocumentationConfig implements WebMvcConfigurer {

    /**
     * Registers custom resource handlers to serve OpenAPI documentation files
     * such as JSON and YAML from a non-standard location (`public.docs`) in the classpath.
     *
     * These files become accessible via browser or Swagger UI under specified URL paths.
     *
     * @param registry the registry to which custom resource handlers are added
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Handler for version 2 OpenAPI JSON documentation
        registry.addResourceHandler("/api/docs/api_version_2.json")
                .addResourceLocations("classpath:/public.docs/api_version_2.json")
                .setCachePeriod(3600) // cache for 1 hour
                .resourceChain(true);

        // Handler for version 2 OpenAPI YAML documentation
        registry.addResourceHandler("/api/docs/api_version_2.yaml")
                .addResourceLocations("classpath:/public.docs/api_version_2.yaml")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Handler for general OpenAPI definition in YAML format
        registry.addResourceHandler("/api/docs/openapi_definition.yaml")
                .addResourceLocations("classpath:/public.docs/openapi_definition.yaml")
                .setCachePeriod(3600)
                .resourceChain(true);

        // Handler for OpenAPI structure in JSON format
        registry.addResourceHandler("/api/docs/openapi_structure.json")
                .addResourceLocations("classpath:/public.docs/openapi_structure.json")
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}
