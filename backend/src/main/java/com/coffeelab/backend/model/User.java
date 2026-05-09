package com.coffeelab.backend.model;

import java.time.LocalDateTime;

public class User {
    public Long id;
    public String nickname;
    public String phone;
    public String email;
    public String passwordHash;
    public String avatarUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
