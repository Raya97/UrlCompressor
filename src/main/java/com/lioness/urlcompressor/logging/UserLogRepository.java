package com.lioness.urlcompressor.logging;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserLogRepository provides CRUD operations for the UserLog entity.
 * It extends JpaRepository, giving access to standard JPA methods such as
 * save(), findById(), findAll(), deleteById(), etc.
 */
public interface UserLogRepository extends JpaRepository<UserLog, Long> {
    // You can define custom query methods here if needed
}
