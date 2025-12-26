package com.linkly.backend.controllers;

import com.linkly.backend.dto.OriginalUrlRequest;
import com.linkly.backend.dto.OriginalUrlResponse;
import com.linkly.backend.dto.ShortenUrlRequest;
import com.linkly.backend.dto.ShortenUrlResponse;
import com.linkly.backend.models.Url;
import com.linkly.backend.services.DeleteUrlRequest;
import com.linkly.backend.services.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/url")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @Value("${app.backend.url:http://localhost:8000}")
    private String backendUrl;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(
            @RequestBody ShortenUrlRequest request,
            HttpServletRequest httpRequest) {

        try {
            // Get authenticated user email from filter
            String email = (String) httpRequest.getAttribute("email");

            if (email == null) {
                return ResponseEntity.status(401)
                        .body(new ShortenUrlResponse(false, null, "User not authenticated"));
            }

            // Validate long URL
            if (request.getLongUrl() == null || request.getLongUrl().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ShortenUrlResponse(false, null, "Long URL is required"));
            }

            // Shorten URL
            Url url = urlService.shortenUrl(
                    request.getLongUrl(),
                    request.getCustomShort(),
                    request.getMaxClicks(),
                    request.getExpiresAt(),
                    email
            );

            String fullShortUrl = backendUrl + "/" + url.getShortUrl();

            return ResponseEntity.ok(
                    new ShortenUrlResponse(true, fullShortUrl, url.getQrCode(), "URL shortened successfully")
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ShortenUrlResponse(false, null, e.getMessage()));
        }
    }

    @PostMapping("/original")
    public ResponseEntity<?> getOriginalUrl(
            @RequestBody OriginalUrlRequest request,
            HttpServletRequest httpRequest) {

        try {
            String email = (String) httpRequest.getAttribute("email");

            if (email == null) {
                return ResponseEntity.status(401)
                        .body(new OriginalUrlResponse(false, null, "User not authenticated"));
            }

            if (request.getShortUrl() == null || request.getShortUrl().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new OriginalUrlResponse(false, null, "Short URL is required"));
            }

            String longUrl = urlService.getOriginalUrl(request.getShortUrl());

            return ResponseEntity.ok(
                    new OriginalUrlResponse(true, longUrl, "Original URL retrieved successfully")
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new OriginalUrlResponse(false, null, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyUrls(HttpServletRequest httpRequest) {
        try {
            String email = (String) httpRequest.getAttribute("email");

            if (email == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("status", false, "message", "User not authenticated"));
            }

            List<Url> urls = urlService.getUserUrls(email);

            return ResponseEntity.ok(
                    Map.of(
                            "status", true,
                            "urls", urls,
                            "count", urls.size()
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUrl(
            @RequestBody DeleteUrlRequest request,
            HttpServletRequest httpRequest) {

        try {
            String email = (String) httpRequest.getAttribute("email");

            if (email == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("status", false, "message", "User not authenticated"));
            }

            if (request.getShortUrl() == null || request.getShortUrl().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", false, "message", "Short URL is required"));
            }

            urlService.deleteUrl(request.getShortUrl(), email);

            return ResponseEntity.ok(
                    Map.of(
                            "status", true,
                            "message", "URL deleted successfully"
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", false, "message", e.getMessage()));
        }
    }
}
