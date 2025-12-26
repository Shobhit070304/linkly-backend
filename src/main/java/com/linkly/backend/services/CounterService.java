package com.linkly.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class CounterService {

    @Autowired
    private RedisService redisService;

    private static final String COUNTER_KEY = "url:counter";

    public long getNextId() {
        Long id = redisService.increment(COUNTER_KEY);

        // If Redis fails or returns null, fallback to a default
        if (id == null) {
            System.err.println("⚠️ Redis counter failed, using fallback");
            return System.currentTimeMillis() % 1000000;
        }

        // Start from 1000 for better looking short URLs
        if (id < 1000) {
            redisService.set(COUNTER_KEY, "1000");
            return 1000;
        }

        return id;
    }
}
