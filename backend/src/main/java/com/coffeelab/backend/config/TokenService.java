package com.coffeelab.backend.config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TokenService {
    private final Map<String, Long> tokenUserIds = new ConcurrentHashMap<>();

    public String issue(Long userId) {
        String token = "mock-" + userId + "-" + UUID.randomUUID();
        tokenUserIds.put(token, userId);
        return token;
    }

    public Long parse(String token) {
        return tokenUserIds.get(token);
    }
}
