package com.lioness.urlcompressor.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ✅ Unit tests for UserServiceImpl — specifically the findByLogin() method.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testFindByLogin_success() {
        // Arrange — mock a user entity
        UserEntity user = new UserEntity();
        user.setLogin("lioness");

        when(userRepository.findByLoginIgnoreCase("lioness")).thenReturn(Optional.of(user));

        // Act
        Optional<UserEntity> result = userService.findByLogin("lioness");

        // Assert
        assertTrue(result.isPresent(), "User should be found");
        assertEquals("lioness", result.get().getLogin());
    }

    @Test
    void testFindByLogin_notFound_shouldReturnEmpty() {
        // Arrange
        when(userRepository.findByLoginIgnoreCase("ghost")).thenReturn(Optional.empty());

        // Act
        Optional<UserEntity> result = userService.findByLogin("ghost");

        // Assert
        assertFalse(result.isPresent(), "User should not be found");
    }

}
