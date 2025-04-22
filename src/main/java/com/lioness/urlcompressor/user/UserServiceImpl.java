package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.security.JwtTokenProvider;
import com.lioness.urlcompressor.user.dto.AuthenticationResponse;
import com.lioness.urlcompressor.user.dto.RegistrationResponse;
import com.lioness.urlcompressor.user.dto.UserRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

/**
 * UserServiceImpl handles user registration and authentication logic.
 * It includes role assignment, password encoding, token generation, and logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Validates that a raw password matches the encoded hash.
     *
     * @param rawPassword     the plain text password
     * @param encodedPassword the hashed password stored in DB
     * @return true if passwords match
     */
    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Finds a user by login (case-insensitive).
     *
     * @param login the username
     * @return Optional of UserEntity
     */
    @Override
    public Optional<UserEntity> findByLogin(String login) {
        return userRepository.findByLoginIgnoreCase(login);
    }

    /**
     * Registers a standard USER account.
     *
     * @param credentials login and password
     * @return registration result with status
     */
    @Override
    public RegistrationResponse register(UserRequestDTO credentials) {
        if (credentials == null || credentials.getLogin() == null || credentials.getPassword() == null) {
            log.warn("Invalid registration data: {}", credentials);
            return RegistrationResponse.failed("Login and password are required", HttpStatus.BAD_REQUEST);
        }

        String login = credentials.getLogin().toLowerCase();

        if (userRepository.existsByLogin(login)) {
            log.warn("Duplicate registration attempt: {}", login);
            return RegistrationResponse.failed("User already exists", HttpStatus.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(credentials.getPassword());

        UserEntity user = UserEntity.builder()
                .login(login)
                .passwordHash(encodedPassword)
                .role(Role.USER)
                .build();

        try {
            userRepository.save(user);
            log.info("New user registered: {}", login);
            return RegistrationResponse.success(user.getId(), user.getLogin());
        } catch (Exception e) {
            log.error("User save failed '{}': {}", login, e.getMessage());
            return RegistrationResponse.failed("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Registers an ADMIN account manually.
     *
     * @param request user credentials
     * @return registration response
     */
    @PostMapping("/register-admin")
    public RegistrationResponse registerAdmin(UserRequestDTO request) {
        if (request == null || request.getLogin() == null || request.getPassword() == null) {
            log.warn("Invalid admin registration request");
            return RegistrationResponse.failed("Login and password are required", HttpStatus.BAD_REQUEST);
        }

        String login = request.getLogin().toLowerCase();

        if (userRepository.existsByLogin(login)) {
            return RegistrationResponse.failed("User already exists", HttpStatus.CONFLICT);
        }

        UserEntity user = UserEntity.builder()
                .login(login)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
        return RegistrationResponse.success(user.getId(), user.getLogin());
    }

    /**
     * Authenticates the user with login credentials.
     *
     * @param credentials login and password
     * @return authentication result with JWT tokens
     */
    @Override
    public AuthenticationResponse authenticate(UserRequestDTO credentials) {
        if (credentials == null || credentials.getLogin() == null || credentials.getPassword() == null) {
            log.warn("Invalid login data: {}", credentials);
            return AuthenticationResponse.failed("Login and password are required");
        }

        String login = credentials.getLogin().toLowerCase();

        UserEntity user = userRepository.findByLoginIgnoreCase(login)
                .orElseThrow(() -> {
                    log.warn("Login failed for non-existent user: {}", login);
                    return new UsernameNotFoundException("Invalid login or password");
                });

        if (!passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash())) {
            log.warn("Incorrect password for login: {}", login);
            return AuthenticationResponse.failed("Invalid login or password");
        }

        log.info("Authenticated role: {}", user.getRole());

        String accessToken = jwtTokenProvider.generateToken(user.getLogin(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getLogin(), user.getRole());

        log.info("Successful login for user: {}", login);
        return AuthenticationResponse.success(accessToken, refreshToken);
    }
}
