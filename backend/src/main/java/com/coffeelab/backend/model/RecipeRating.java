package com.coffeelab.backend.model;

import java.time.LocalDateTime;

public class RecipeRating {
    public Long id;
    public Long publicRecipeId;
    public Long userId;
    public int score;
    public String comment;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
