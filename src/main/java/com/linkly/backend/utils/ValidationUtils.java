package com.linkly.backend.utils;

public class ValidationUtils {

    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        // Basic URL validation
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static boolean isValidCustomShort(String customShort) {
        if (customShort == null || customShort.isEmpty()) {
            return true;  // Optional field
        }

        // Only alphanumeric and hyphens, 3-50 chars
        return customShort.matches("^[a-zA-Z0-9-]{3,50}$");
    }

    public static boolean isReservedKeyword(String shortUrl) {
        // Reserved paths that shouldn't be used as short URLs
        String[] reserved = {"api", "admin", "login", "health", "static", "public"};

        for (String keyword : reserved) {
            if (shortUrl.equalsIgnoreCase(keyword)) {
                return true;
            }
        }
        return false;
    }
}