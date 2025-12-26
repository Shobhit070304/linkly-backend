package com.linkly.backend.controllers;

import com.linkly.backend.models.HealthResponse;
import com.linkly.backend.repositories.UrlRepository;
import com.linkly.backend.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UrlRepository urlRepository;

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "success");
        health.put("message", "Backend is running! 🚀");
        health.put("timestamp", System.currentTimeMillis());

        // Test MongoDB
        try {
            long count = urlRepository.count();
            health.put("database", "connected");
            health.put("totalUrls", count);
        } catch (Exception e) {
            health.put("database", "error");
        }

        // Test Redis
        try {
            redisService.set("health:test", "ok");
            String value = redisService.get("health:test");
            health.put("redis", value != null ? "connected" : "disconnected");
        } catch (Exception e) {
            health.put("redis", "error");
        }

        return health;
    }
}
