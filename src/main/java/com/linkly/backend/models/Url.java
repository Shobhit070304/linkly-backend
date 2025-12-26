package com.linkly.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "urls")
public class Url {
    @Id
    private String id;

    private String longUrl;
    private String shortUrl;
    private String customShort;

    private String userId;  // Reference to User
    private int clicks;

    private LocalDateTime createdAt;

    // New fields for metadata
    private String qrCode;        // Base64 QR code image
    private String title;         // Page title
    private String description;   // Page description
    private String favicon;       // Favicon URL

    // Expiry & Limit fields (NEW)
    private Integer maxClicks;        // Maximum clicks allowed (null = unlimited)
    private LocalDateTime expiresAt;  // Expiration date (null = never expires)

    // Constructor for creating new URL
    public Url(String longUrl, String shortUrl, String customShort, String userId) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.customShort = customShort;
        this.userId = userId;
        this.clicks = 0;
        this.createdAt = LocalDateTime.now();
    }
}
