package com.linkly.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OriginalUrlResponse {
    private boolean status;
    private String longUrl;
    private String message;
}
