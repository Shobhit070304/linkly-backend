package com.linkly.backend.controllers;

import com.linkly.backend.dto.LoginRequest;
import com.linkly.backend.models.User;
import com.linkly.backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            HttpServletRequest httpRequest) {

        // Filter se user info extract karo
        String email = (String) httpRequest.getAttribute("email");
        String name = (String) httpRequest.getAttribute("name");

        // Find or create user
        User user = userService.findOrCreateUser(name, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    // Test endpoint - Create user
    @PostMapping("/test-create")
    public ResponseEntity<Map<String, Object>> testCreateUser(
            @RequestBody Map<String, String> request) {

        String name = request.get("name");
        String email = request.get("email");

        User user = userService.findOrCreateUser(name, email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}
