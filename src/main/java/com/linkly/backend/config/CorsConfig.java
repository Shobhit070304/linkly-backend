package com.linkly.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                frontendUrl
        ));

        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(Arrays.asList("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        CorsFilter corsFilter = new CorsFilter(source);

        // ✅ THIS IS THE KEY LINE
        FilterRegistrationBean<CorsFilter> registration =
                new FilterRegistrationBean<>(corsFilter);
        registration.setOrder(0); // HIGHEST priority

        return registration;
    }
}
