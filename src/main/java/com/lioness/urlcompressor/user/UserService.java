package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.user.dto.AuthenticationResponse;
import com.lioness.urlcompressor.user.dto.RegistrationResponse;
import com.lioness.urlcompressor.user.dto.UserRequestDTO;

import java.util.Optional;

/**
 * UserService defines core user-related operations such as registration, authentication,
 * and user lookup by login.
 */
public interface UserService {

    /**
     * Registers a new user with the provided credentials.
     *
     * @param credentials DTO containing username and password
     * @return registration result with user ID and status
     */
    RegistrationResponse register(UserRequestDTO credentials);

    /**
     * Authenticates the user using the provided credentials.
     *
     * @param credentials DTO containing login and password
     * @return authentication response including tokens if successful
     */
    AuthenticationResponse authenticate(UserRequestDTO credentials);

    /**
     * Retrieves a user entity by login (username).
     *
     * @param login username to look up
     * @return Optional containing the user if found, otherwise empty
     */
    Optional<UserEntity> findByLogin(String login);
}
