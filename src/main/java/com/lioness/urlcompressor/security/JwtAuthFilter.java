package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.user.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JwtAuthFilter is a custom Spring Security filter that processes incoming HTTP requests,
 * extracts the JWT token from the Authorization header, validates it,
 * and sets the user details in the SecurityContext if the token is valid.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * This method is called once per request to perform filtering logic.
     * It attempts to extract and validate a JWT token, and populate the SecurityContext.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException in case of a servlet error
     * @throws IOException      in case of an I/O error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = jwtTokenProvider.extractTokenFromHeader(authHeader);

            // Validate the extracted token
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.extractUsernameFromToken(token);
                Role role = jwtTokenProvider.extractRoleFromToken(token);

                if (username != null && role != null) {
                    // Create authority based on user role
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    // Create authenticated token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    // Attach request details to the authentication token
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Set authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                log.warn("Invalid JWT token");
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
