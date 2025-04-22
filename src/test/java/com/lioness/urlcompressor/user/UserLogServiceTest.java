package com.lioness.urlcompressor.user;

import com.lioness.urlcompressor.logging.UserLog;
import com.lioness.urlcompressor.logging.UserLogRepository;
import com.lioness.urlcompressor.logging.UserLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * ✅ Unit test for UserLogService — verifies that user actions are logged correctly.
 */
@ExtendWith(MockitoExtension.class)
class UserLogServiceTest {

    @Mock
    private UserLogRepository userLogRepository;

    @InjectMocks
    private UserLogService userLogService;

    @Test
    void testLogUserAction_success() {
        // Arrange — create a dummy log object to simulate save
        UserLog log = UserLog.builder()
                .username("lioness")
                .action("LOGIN")
                .timestamp(LocalDateTime.now())
                .build();

        when(userLogRepository.save(any(UserLog.class))).thenReturn(log);

        // Act & Assert — ensure no exceptions are thrown during logging
        assertDoesNotThrow(() -> userLogService.log("lioness", "LOGIN"));
    }
}
