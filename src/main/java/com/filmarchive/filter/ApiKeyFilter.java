package com.filmarchive.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class ApiKeyFilter implements Filter {

    private static final String API_KEY_HEADER = "X-API-Key";

    // Public endpoints — no key required
    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/api/photos",
            "/api/film-rolls",
            "/api/cameras"
    );

    // Write methods that require the API key
    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "DELETE", "PATCH");

    @Value("${app.api-key}")
    private String apiKey;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String method = request.getMethod();
        String path = request.getRequestURI();

        String authHeader = request.getHeader("Authorization");
        boolean hasBearerToken = authHeader != null && authHeader.startsWith("Bearer ");

        if (WRITE_METHODS.contains(method) && !hasBearerToken && !path.startsWith("/api/auth/")) {
            String key = request.getHeader(API_KEY_HEADER);
            if (!apiKey.equals(key)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid or missing API key\"}");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
