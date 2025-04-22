package com.lioness.urlcompressor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for launching the Spring Boot application.
 * Initializes all necessary components and starts the application.
 */
@EnableScheduling
@SpringBootApplication
public class UrlCompressorApp {

    /**
     * Entry point of the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("🚀 Starting UrlCompressorApp...");
        SpringApplication.run(UrlCompressorApp.class, args);
    }
}
