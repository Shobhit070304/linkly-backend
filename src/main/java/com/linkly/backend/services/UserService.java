package com.linkly.backend.services;

import com.linkly.backend.models.User;
import com.linkly.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Create new user
    public User createUser(String name, String email) {
        User user = new User(name, email);
        return userRepository.save(user);
    }

    // Find or create user
    public User findOrCreateUser(String name, String email) {
        System.out.println("🔍 Searching for user: " + email);

        Optional<User> existingUser = findByEmail(email);

        if (existingUser.isPresent()) {
            System.out.println("✅ User found: " + existingUser.get().getName());
            return existingUser.get();
        }

        System.out.println("➕ Creating new user: " + name);
        return createUser(name, email);
    }
}
