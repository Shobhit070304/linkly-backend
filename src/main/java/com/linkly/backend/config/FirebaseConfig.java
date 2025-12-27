package com.linkly.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials credentials;
            
            // Try environment variable first (for deployment)
            String firebaseConfig = System.getenv("FIREBASE_CONFIG");
            
            if (firebaseConfig != null && !firebaseConfig.isEmpty()) {
                // Production: Use environment variable
                System.out.println("🔥 Loading Firebase from environment variable...");
                ByteArrayInputStream stream = new ByteArrayInputStream(
                    firebaseConfig.getBytes(StandardCharsets.UTF_8)
                );
                credentials = GoogleCredentials.fromStream(stream);
            } else {
                // Development: Use local file
                System.out.println("🔥 Loading Firebase from classpath...");
                InputStream resource = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");
                
                if (resource == null) {
                    throw new IOException("firebase-service-account.json not found in resources");
                }
                
                credentials = GoogleCredentials.fromStream(resource);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialized successfully!");
            }

        } catch (IOException e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
