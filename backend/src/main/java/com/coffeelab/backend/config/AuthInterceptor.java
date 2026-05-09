package com.coffeelab.backend.config;

import com.coffeelab.backend.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/auth/", "/api/classic-coffees", "/api/public-recipes", "/api/rankings/",
            "/v3/api-docs", "/swagger-ui", "/swagger-ui.html");
    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublic(request)) {
            return true;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new BusinessException(401, "unauthorized");
        }
        Long userId = tokenService.parse(header.substring("Bearer ".length()).trim());
        if (userId == null) {
            throw new BusinessException(401, "unauthorized");
        }
        AuthContext.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private boolean isPublic(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (path.matches("/api/public-recipes/\\d+/ratings/summary") && "GET".equals(method)) {
            return true;
        }
        if (path.matches("/api/public-recipes/\\d+") && "GET".equals(method)) {
            return true;
        }
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith) && "GET".equals(method)
                || path.startsWith("/api/auth/");
    }
}
