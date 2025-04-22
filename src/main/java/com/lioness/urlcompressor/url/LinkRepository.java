package com.lioness.urlcompressor.url;

import com.lioness.urlcompressor.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * LinkRepository provides database access methods for working with shortened URLs.
 * It extends JpaRepository for standard CRUD operations and includes custom queries.
 */
@Repository
public interface LinkRepository extends JpaRepository<UrlEntity, Long> {

    /**
     * Finds a URL entity by its shortened form.
     *
     * @param shortUrl the short version of the URL
     * @return an Optional containing the UrlEntity if found
     */
    Optional<UrlEntity> findByShortenedUrl(String shortUrl);

    /**
     * Retrieves all shortened URLs owned by a specific user.
     *
     * @param owner the user who created the URLs
     * @return a list of UrlEntity objects belonging to the user
     */
    List<UrlEntity> findByOwner(UserEntity owner);
}
