package com.lioness.urlcompressor.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * ApplicationExceptionHandler is a centralized error handling mechanism.
 * It catches all unhandled exceptions and renders a custom error view.
 */
@ControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    // Reads the currently active Spring profile (defaults to 'prod' if not set)
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    /**
     * Global exception handler for all uncaught exceptions.
     * Returns a formatted HTML error page using a Thymeleaf or JSP view.
     *
     * @param exception the thrown exception
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @return ModelAndView pointing to the error-view template
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        // Log the error with stack trace
        log.error("An unexpected error occurred: {}", exception.getMessage(), exception);

        // Set HTTP response status code to 500
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Create the error view and populate it with basic info
        ModelAndView model = new ModelAndView("error-view");
        model.addObject("timestamp", LocalDateTime.now());
        model.addObject("message", "An unexpected error occurred. Please check the logs or contact support.");
        model.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        model.addObject("exception", exception.getClass().getSimpleName());
        model.addObject("path", request.getRequestURI());

        // Include stack trace in dev mode for easier debugging
        if ("dev".equalsIgnoreCase(activeProfile)) {
            StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            model.addObject("trace", sw.toString());
        } else {
            // Do not show trace in production for security reasons
            model.addObject("trace", "");
        }

        return model;
    }
}
