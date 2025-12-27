package com.linkly.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize(){
        try {
            // Load from classpath (works in JAR/Docker)
            var resource = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");
            
            if (resource == null) {
                throw new IOException("firebase-service-account.json not found in resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase initialized successfully!");
            }

        } catch (IOException e) {
            System.err.println("❌ Firebase initialization failed: " + e.getMessage());
        }
    }
}
