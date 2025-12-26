package com.linkly.backend.dto;

import lombok.Data;

@Data
public class ShortenUrlRequest {
    private String longUrl;
    private String customShort;
    private Integer maxClicks;
    private String expiresAt;
}
