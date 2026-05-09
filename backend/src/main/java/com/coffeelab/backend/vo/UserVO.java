package com.coffeelab.backend.vo;

import com.coffeelab.backend.model.User;
import java.time.LocalDateTime;

public record UserVO(Long id, String nickname, String phone, String email, String avatarUrl, LocalDateTime createdAt) {
    public static UserVO from(User user) {
        return new UserVO(user.id, user.nickname, user.phone, user.email, user.avatarUrl, user.createdAt);
    }
}
