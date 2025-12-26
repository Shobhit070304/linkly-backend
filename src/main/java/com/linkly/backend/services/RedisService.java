package com.linkly.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Set key-value with expiration
    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            System.out.println("✅ Redis SET: " + key);
        } catch (Exception e) {
            System.err.println("❌ Redis SET failed: " + e.getMessage());
        }
    }

    // Set key-value without expiration
    public void set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            System.out.println("✅ Redis SET: " + key);
        } catch (Exception e) {
            System.err.println("❌ Redis SET failed: " + e.getMessage());
        }
    }

    // Get value by key
    public String get(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                System.out.println("✅ Redis GET: " + key + " (HIT)");
            } else {
                System.out.println("⚠️ Redis GET: " + key + " (MISS)");
            }
            return value;
        } catch (Exception e) {
            System.err.println("❌ Redis GET failed: " + e.getMessage());
            // Return null, application will fall back to database
            return null;
        }
    }

    // Delete key
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            System.out.println("✅ Redis DELETE: " + key);
        } catch (Exception e) {
            System.err.println("❌ Redis DELETE failed: " + e.getMessage());
        }
    }

    // Increment counter
    public Long increment(String key) {
        try {
            Long value = redisTemplate.opsForValue().increment(key);
            System.out.println("✅ Redis INCREMENT: " + key + " → " + value);
            return value;
        } catch (Exception e) {
            System.err.println("❌ Redis INCREMENT failed: " + e.getMessage());
            return null;
        }
    }

    // Check if key exists
    public boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return exists != null && exists;
        } catch (Exception e) {
            System.err.println("❌ Redis hasKey failed: " + e.getMessage());
            return false;
        }
    }
}
