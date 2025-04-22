package com.lioness.urlcompressor.security;

import com.lioness.urlcompressor.user.Role;
import com.lioness.urlcompressor.user.UserEntity;
import com.lioness.urlcompressor.user.UserRepository;
import com.lioness.urlcompressor.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthorizationService authorizationService;
    private UserServiceImpl userService;

    private static final String TOKEN_HEADER = "Bearer valid.token";
    private static final String TOKEN = "valid.token";
    private static final String USERNAME = "lioness";

    @BeforeEach
    void setup() {
        // Create UserServiceImpl with mocks (not actually used here)
        userService = new UserServiceImpl(jwtTokenProvider, userRepository, passwordEncoder);
        authorizationService = new AuthorizationService(userRepository, jwtTokenProvider);

        UserEntity user = UserEntity.builder()
                .login(USERNAME)
                .role(Role.USER)
                .passwordHash("encoded") // password is irrelevant for these tests
                .build();

        // Flexible mock: extract token from Authorization header if it starts with "Bearer "
        when(jwtTokenProvider.extractTokenFromHeader(anyString())).thenAnswer(invocation -> {
            String header = invocation.getArgument(0);
            return header.startsWith("Bearer ") ? header.substring(7) : null;
        });
    }

    @Test
    @DisplayName("getAuthorizedUser: successful authorization")
    void getAuthorizedUser_success() {
        // Simulate valid token and user lookup
        when(jwtTokenProvider.extractTokenFromHeader(TOKEN_HEADER)).thenReturn(TOKEN);
        when(jwtTokenProvider.validateToken(TOKEN)).thenReturn(true);
        when(jwtTokenProvider.extractUsernameFromToken(TOKEN)).thenReturn(USERNAME);

        UserEntity mockUser = UserEntity.builder().login(USERNAME).build();
        when(userRepository.findByLoginIgnoreCase(USERNAME)).thenReturn(Optional.of(mockUser));

        Optional<UserEntity> result = authorizationService.getAuthorizedUser(TOKEN_HEADER);

        assertTrue(result.isPresent(), "User should be authorized");
        assertEquals(USERNAME, result.get().getLogin());
    }

    @Test
    @DisplayName("getAuthorizedUser: token without 'Bearer' prefix")
    void getAuthorizedUser_tokenWithoutBearer_shouldReturnEmpty() {
        Optional<UserEntity> result = authorizationService.getAuthorizedUser("invalid-token");

        assertTrue(result.isEmpty(), "Expected empty result for token without 'Bearer' prefix");
    }

    @Test
    @DisplayName("getAuthorizedUser: user not found")
    void getAuthorizedUser_userNotFound_shouldReturnEmpty() {
        // Simulate valid token, but user doesn't exist
        when(jwtTokenProvider.extractTokenFromHeader(TOKEN_HEADER)).thenReturn(TOKEN);
        when(jwtTokenProvider.validateToken(TOKEN)).thenReturn(true);
        when(jwtTokenProvider.extractUsernameFromToken(TOKEN)).thenReturn(USERNAME);
        when(userRepository.findByLoginIgnoreCase(USERNAME)).thenReturn(Optional.empty());

        Optional<UserEntity> result = authorizationService.getAuthorizedUser(TOKEN_HEADER);

        assertTrue(result.isEmpty(), "Expected empty result when user not found");
    }

    @Test
    @DisplayName("getAuthorizedUser: invalid token")
    void getAuthorizedUser_invalidToken_shouldReturnEmpty() {
        // Simulate invalid token
        when(jwtTokenProvider.validateToken(TOKEN)).thenReturn(false);

        Optional<UserEntity> result = authorizationService.getAuthorizedUser(TOKEN_HEADER);

        assertTrue(result.isEmpty(), "Invalid token should return empty result");
    }
}
