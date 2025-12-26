package com.linkly.backend.controllers;

import com.linkly.backend.models.Url;
import com.linkly.backend.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
public class RedirectController {

    @Autowired
    private UrlService urlService;

    @GetMapping("/{shortUrl}")
    public Object redirect(@PathVariable String shortUrl) {

        System.out.println("🔀 Redirect request for: " + shortUrl);

        // Find URL
        Optional<Url> urlOpt = urlService.findByShortUrl(shortUrl);

        if (urlOpt.isEmpty()) {
            System.out.println("❌ Short URL not found: " + shortUrl);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", false,
                            "message", "Short URL not found"
                    ));
        }

        Url url = urlOpt.get();

        // Check if expired
        if (url.getExpiresAt() != null && LocalDateTime.now().isAfter(url.getExpiresAt())) {
            System.out.println("⏰ URL expired: " + shortUrl);

            // Delete expired URL
            urlService.deleteExpiredUrl(url);

            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of(
                            "status", false,
                            "message", "This link has expired"
                    ));
        }

        // Check click limit
        if (url.getMaxClicks() != null && url.getClicks() >= url.getMaxClicks()) {
            System.out.println("🚫 Click limit reached: " + shortUrl);

            // Delete URL that reached limit
            urlService.deleteExpiredUrl(url);

            return ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of(
                            "status", false,
                            "message", "This link has reached its click limit"
                    ));
        }

        // Increment clicks
        urlService.incrementClicks(shortUrl);

        // Redirect to long URL
        System.out.println("✅ Redirecting to: " + url.getLongUrl());
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url.getLongUrl())
                .build();
    }
}
