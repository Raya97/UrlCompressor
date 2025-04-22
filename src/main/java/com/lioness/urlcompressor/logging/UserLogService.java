package com.lioness.urlcompressor.logging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * UserLogService handles the creation and persistence of user activity logs.
 * It records actions such as login, logout, and token refresh events.
 */
@Service
@RequiredArgsConstructor
public class UserLogService {

    // Repository for persisting UserLog entries to the database
    private final UserLogRepository userLogRepository;

    /**
     * Logs a user action (e.g., login, logout, refresh) with the current timestamp.
     *
     * @param username the name of the user performing the action
     * @param action the type of action performed (login, logout, refresh, etc.)
     */
    public void log(String username, String action) {
        UserLog log = UserLog.builder()
                .username(username)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();

        userLogRepository.save(log);
    }
}
