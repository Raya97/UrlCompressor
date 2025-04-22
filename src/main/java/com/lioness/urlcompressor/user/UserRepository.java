package com.lioness.urlcompressor.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository handles all database operations related to UserEntity.
 * Provides basic CRUD functionality along with custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Checks if a user exists with the specified login.
     *
     * @param login the username to check
     * @return true if a user with the given login exists
     */
    boolean existsByLogin(String login);

    /**
     * Finds a user by login (case-insensitive).
     *
     * @param login the username to search
     * @return Optional containing the user if found, otherwise empty
     */
    Optional<UserEntity> findByLoginIgnoreCase(String login);

    /**
     * Deletes a user by login (case-insensitive).
     * Useful for cleanup in tests or admin operations.
     *
     * @param login the username to delete
     */
    void deleteByLoginIgnoreCase(String login); // ✅ Used in test cleanup
}
