package com.linkly.backend.repositories;

import com.linkly.backend.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Custom query method
    Optional<User> findByEmail(String email);
}
