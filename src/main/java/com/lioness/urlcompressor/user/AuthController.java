package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.security.TokenBlacklistService;
import com.lioness.urlcompressor.logging.UserLogService;
import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.user.dto.AuthenticationResponse;
import com.lioness.urlcompressor.user.dto.RefreshRequestDTO;
import com.lioness.urlcompressor.user.dto.RegistrationResponse;
import com.lioness.urlcompressor.user.dto.UserRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * AuthController handles all authentication-related endpoints,
 * including user registration, login, admin creation, and token refreshing.
 */
@RestController
@RequestMapping("/api/v1/user/")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserLogService userLogService;

    /**
     * Registers a regular user.
     *
     * @param credentials user-provided login and password
     * @return HTTP response with user registration status
     */
    @PostMapping("/signup")
    public ResponseEntity<RegistrationResponse> signUp(@RequestBody @Valid UserRequestDTO credentials) {
        RegistrationResponse response = userService.register(credentials);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Registers an admin user.
     *
     * @param request admin credentials
     * @return HTTP response with admin registration status
     */
    @PostMapping("/register-admin")
    public ResponseEntity<RegistrationResponse> registerAdmin(@RequestBody @Valid UserRequestDTO request) {
        RegistrationResponse response = userService.registerAdmin(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    /**
     * Authenticates a user and returns access/refresh tokens.
     *
     * @param credentials user-provided login and password
     * @return authentication response with tokens or error message
     */
    @PostMapping("/signin")
    @Cacheable(value = "authCache", key = "#credentials.login")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody @Valid UserRequestDTO credentials) {
        UserEntity user;
        try {
            user = userService.findByLogin(credentials.getLogin())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.failed("User not found"));
        }

        if (!userService.passwordMatches(credentials.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.failed("Invalid password"));
        }

        String accessToken = jwtTokenProvider.generateToken(user.getLogin(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getLogin(), user.getRole());

        return ResponseEntity.ok(new AuthenticationResponse("Authentication successful", accessToken, refreshToken));
    }

    /**
     * Refreshes access and refresh tokens using a valid refresh token.
     *
     * @param request DTO containing the refresh token
     * @return new set of tokens or an error response
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody @Valid RefreshRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        System.out.println("🔁 Received refresh token: " + refreshToken);

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.failed("This token is no longer active (logged out)"));
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.failed("This token is no longer valid (invalid or expired)"));
        }

        String username = jwtTokenProvider.extractUsernameFromToken(refreshToken);
        UserEntity user = userService.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Duration ttl = jwtTokenProvider.getTokenTTL(refreshToken);
        tokenBlacklistService.blacklist(refreshToken, ttl);

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getLogin(), user.getRole());
        String newAccessToken = jwtTokenProvider.generateToken(user.getLogin(), user.getRole());

        userLogService.log(user.getLogin(), "refresh");

        return ResponseEntity.ok(AuthenticationResponse.success(newAccessToken, newRefreshToken));
    }
}
