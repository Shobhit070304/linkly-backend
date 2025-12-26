package com.linkly.backend.filters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ Skip auth for redirect like /abc123 (only one path segment)
        if (path.startsWith("/") && path.substring(1).contains("/") == false
                && !path.equals("/health")) {
            filterChain.doFilter(request, response);
            return;
        }


        // ✅ Allow CORS preflight BEFORE auth
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Public routes (NO AUTH)
        if (path.equals("/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Token missing. Please login.");
            return;
        }

        try {
            String token = authHeader.substring(7);
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            request.setAttribute("uid", decodedToken.getUid());
            request.setAttribute("email", decodedToken.getEmail());
            request.setAttribute("name", decodedToken.getName());

            System.out.println("✅ User authenticated: " + decodedToken.getEmail());

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.err.println("❌ Token verification failed: " + e.getMessage());
            sendErrorResponse(response, "Invalid or expired token. Please login again.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"success\": false, \"error\": \"" + message + "\"}"
        );
    }
}