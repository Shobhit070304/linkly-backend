package com.linkly.backend.services;

import com.linkly.backend.models.Url;
import com.linkly.backend.models.User;
import com.linkly.backend.repositories.UrlRepository;
import com.linkly.backend.repositories.UserRepository;
import com.linkly.backend.utils.Base62Encoder;
import com.linkly.backend.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CounterService counterService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private MetadataService metadataService;

    @Value("${app.backend.url:http://localhost:8000}")
    private String backendUrl;

    private static final String URL_CACHE_PREFIX = "url:";

    public Url shortenUrl(String longUrl, String customShort, Integer maxClicks, String expiresAt, String userEmail) {

        System.out.println("🔗 Shortening URL: " + longUrl);

        // Check Redis cache first
        String cachedShortUrl = redisService.get(URL_CACHE_PREFIX + longUrl);
        if (cachedShortUrl != null) {
            System.out.println("⚡ Found in cache: " + cachedShortUrl);
            Optional<Url> cachedUrl = urlRepository.findByShortUrl(cachedShortUrl);
            if (cachedUrl.isPresent()) {
                return cachedUrl.get();
            }
        }

        // Check database
        Optional<Url> existingUrl = urlRepository.findByLongUrl(longUrl);
        if (existingUrl.isPresent()) {
            System.out.println("✅ URL already exists in DB");
            Url url = existingUrl.get();
            // Cache it for next time
            redisService.set(URL_CACHE_PREFIX + longUrl, url.getShortUrl());
            return url;
        }

        // Find user
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Validate URL format
        if (!ValidationUtils.isValidUrl(longUrl)) {
            throw new RuntimeException("Invalid URL format. Must start with http:// or https://");
        }

        // Generate short URL
        String shortUrl;
        if (customShort != null && !customShort.isEmpty()) {
            if (!ValidationUtils.isValidCustomShort(customShort)) {
                throw new RuntimeException("Custom short can only contain letters, numbers, and hyphens (3-50 chars)");
            }

            if (ValidationUtils.isReservedKeyword(customShort)) {
                throw new RuntimeException("This custom short URL is reserved and cannot be used");
            }

            Optional<Url> customExists = urlRepository.findByCustomShort(customShort);
            if (customExists.isPresent()) {
                throw new RuntimeException("Custom short URL already exists");
            }
            shortUrl = customShort;
        } else {
            long id = counterService.getNextId();
            shortUrl = Base62Encoder.encode(id);
        }

        System.out.println("✨ Generated short URL: " + shortUrl);

        // Create and save URL
        Url url = new Url(longUrl, shortUrl, customShort, user.get().getId());

        // Set expiry fields (NEW)
        if (maxClicks != null && maxClicks > 0) {
            url.setMaxClicks(maxClicks);
            System.out.println("⏱️ Max clicks set to: " + maxClicks);
        }

        System.out.println("Expires at: " + expiresAt);

        if (expiresAt != null && !expiresAt.isEmpty()) {
            try {
                LocalDate expiryDate = LocalDate.parse(expiresAt);
                LocalDateTime expiry = expiryDate.atTime(23, 59, 59);

                if (expiry.isBefore(LocalDateTime.now())) {
                    throw new RuntimeException("Expiry date must be in the future");
                }

                url.setExpiresAt(expiry);
                System.out.println("📅 Expires at: " + expiry);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Invalid date format. Use: YYYY-MM-DD (e.g., 2025-12-31)"
                );
            }
        }



        // Generate QR Code
        String fullShortUrl = backendUrl + "/" + shortUrl;
        String qrCode = qrCodeService.generateQRCode(fullShortUrl);
        url.setQrCode(qrCode);

        // Fetch metadata (async in future, but sync for now)
        try {
            Map<String, String> metadata = metadataService.fetchMetadata(longUrl);
            url.setTitle(metadata.get("title"));
            url.setDescription(metadata.get("description"));
            url.setFavicon(metadata.get("favicon"));
        } catch (Exception e) {
            System.err.println("⚠️ Metadata fetch failed, using defaults");
            url.setTitle(longUrl);
            url.setDescription("");
            url.setFavicon("");
        }

        // Save to database
        Url savedUrl = urlRepository.save(url);

        // Cache the result
        redisService.set(URL_CACHE_PREFIX + longUrl, shortUrl);
        redisService.set(URL_CACHE_PREFIX + "short:" + shortUrl, longUrl);

        System.out.println("💾 URL saved to database and cached");

        return savedUrl;
    }

    public Optional<Url> findByShortUrl(String shortUrl) {
        // Check cache first
        String cachedLongUrl = redisService.get(URL_CACHE_PREFIX + "short:" + shortUrl);
        if (cachedLongUrl != null) {
            System.out.println("⚡ Short URL found in cache");
            Optional<Url> url = urlRepository.findByShortUrl(shortUrl);
            return url;
        }

        // Check database
        Optional<Url> url = urlRepository.findByShortUrl(shortUrl);
        if (url.isPresent()) {
            // Cache for next time
            redisService.set(URL_CACHE_PREFIX + "short:" + shortUrl, url.get().getLongUrl());
        }

        return url;
    }

    public String getOriginalUrl(String shortUrl) {
        System.out.println("🔍 Finding original URL for: " + shortUrl);

        // Remove backend URL if present
        // Example: "http://localhost:8000/abc123" → "abc123"
        String cleanShortUrl = shortUrl.replace(backendUrl + "/", "")
                .replace(backendUrl, "");

        // Check cache first
        String cachedLongUrl = redisService.get(URL_CACHE_PREFIX + "short:" + cleanShortUrl);
        if (cachedLongUrl != null) {
            System.out.println("⚡ Found in cache: " + cachedLongUrl);
            return cachedLongUrl;
        }

        // Check database
        Optional<Url> url = urlRepository.findByShortUrl(cleanShortUrl);
        if (url.isEmpty()) {
            throw new RuntimeException("Short URL not found");
        }

        String longUrl = url.get().getLongUrl();

        // Cache for next time
        redisService.set(URL_CACHE_PREFIX + "short:" + cleanShortUrl, longUrl);

        System.out.println("✅ Original URL found: " + longUrl);
        return longUrl;
    }

    public List<Url> getUserUrls(String userEmail) {
        System.out.println("📋 Fetching URLs for user: " + userEmail);

        // Find user
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Get all URLs by user ID
        List<Url> urls = urlRepository.findByUserId(user.get().getId());

        System.out.println("✅ Found " + urls.size() + " URLs");
        return urls;
    }

    public void deleteUrl(String shortUrl, String userEmail) {
        System.out.println("🗑️ Deleting URL: " + shortUrl);

        // Clean short URL
        String cleanShortUrl = shortUrl.replace(backendUrl + "/", "")
                .replace(backendUrl, "");

        // Find URL
        Optional<Url> urlOpt = urlRepository.findByShortUrl(cleanShortUrl);
        if (urlOpt.isEmpty()) {
            throw new RuntimeException("Short URL not found");
        }

        Url url = urlOpt.get();

        // Find user
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Check ownership
        if (!url.getUserId().equals(user.get().getId())) {
            throw new RuntimeException("You don't have permission to delete this URL");
        }

        // Delete from cache
        redisService.delete(URL_CACHE_PREFIX + url.getLongUrl());
        redisService.delete(URL_CACHE_PREFIX + "short:" + cleanShortUrl);

        // Delete from database
        urlRepository.delete(url);

        System.out.println("✅ URL deleted successfully");
    }

    public void incrementClicks(String shortUrl) {
        Optional<Url> urlOpt = urlRepository.findByShortUrl(shortUrl);
        if (urlOpt.isPresent()) {
            Url url = urlOpt.get();
            url.setClicks(url.getClicks() + 1);
            urlRepository.save(url);
            System.out.println("📊 Click count updated: " + url.getClicks());
        }
    }

    public void deleteExpiredUrl(Url url) {
        System.out.println("🗑️ Deleting expired/limited URL: " + url.getShortUrl());

        // Delete from cache
        redisService.delete(URL_CACHE_PREFIX + url.getLongUrl());
        redisService.delete(URL_CACHE_PREFIX + "short:" + url.getShortUrl());

        // Delete from database
        urlRepository.delete(url);

        System.out.println("✅ Expired URL deleted");
    }
}
