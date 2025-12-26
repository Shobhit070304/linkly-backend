package com.linkly.backend.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetadataService {

    private static final int TIMEOUT = 5000; // 5 seconds

    public Map<String, String> fetchMetadata(String url) {
        Map<String, String> metadata = new HashMap<>();

        try {
            System.out.println("🌐 Fetching metadata for: " + url);

            // Connect and fetch HTML
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .userAgent("Mozilla/5.0")
                    .get();

            // Extract title
            String title = doc.title();
            if (title == null || title.isEmpty()) {
                title = url;
            }
            metadata.put("title", title);

            // Extract description
            String description = getMetaTag(doc, "description");
            if (description == null || description.isEmpty()) {
                description = getMetaTag(doc, "og:description");
            }
            metadata.put("description", description != null ? description : "");

            // Extract favicon
            String favicon = getFaviconUrl(doc, url);
            metadata.put("favicon", favicon != null ? favicon : "");

            System.out.println("✅ Metadata fetched: " + title);

        } catch (IOException e) {
            System.err.println("⚠️ Failed to fetch metadata: " + e.getMessage());
            // Return default values
            metadata.put("title", url);
            metadata.put("description", "");
            metadata.put("favicon", "");
        }

        return metadata;
    }

    private String getMetaTag(Document doc, String attr) {
        // Try meta name first
        Element element = doc.selectFirst("meta[name=" + attr + "]");
        if (element != null) {
            return element.attr("content");
        }

        // Try meta property (for Open Graph)
        element = doc.selectFirst("meta[property=" + attr + "]");
        if (element != null) {
            return element.attr("content");
        }

        return null;
    }

    private String getFaviconUrl(Document doc, String baseUrl) {
        // Try different favicon selectors
        Element link = doc.selectFirst("link[rel~=icon]");
        if (link == null) {
            link = doc.selectFirst("link[rel~=shortcut icon]");
        }

        if (link != null) {
            String href = link.attr("href");
            return resolveUrl(baseUrl, href);
        }

        // Default favicon location
        try {
            URI uri = new URI(baseUrl);
            return uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private String resolveUrl(String baseUrl, String relativeUrl) {
        try {
            URI base = new URI(baseUrl);
            URI resolved = base.resolve(relativeUrl);
            return resolved.toString();
        } catch (URISyntaxException e) {
            return relativeUrl;
        }
    }
}
