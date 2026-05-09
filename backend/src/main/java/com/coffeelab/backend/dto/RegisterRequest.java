package com.coffeelab.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(String nickname, String phone, String email, @NotBlank String password) {
}
