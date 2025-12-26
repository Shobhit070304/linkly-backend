package com.linkly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenUrlResponse {
    private boolean status;
    private String shortUrl;
    private String qrCode;
    private String message;

    public ShortenUrlResponse(boolean status, String shortUrl, String message) {
        this.status = status;
        this.shortUrl = shortUrl;
        this.message = message;
    }
}
