package com.linkly.backend.repositories;

import com.linkly.backend.models.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {

    // Find by long URL
    Optional<Url> findByLongUrl(String longUrl);

    // Find by short URL
    Optional<Url> findByShortUrl(String shortUrl);

    // Find by custom short
    Optional<Url> findByCustomShort(String customShort);

    // Find all URLs by user
    List<Url> findByUserId(String userId);
}
