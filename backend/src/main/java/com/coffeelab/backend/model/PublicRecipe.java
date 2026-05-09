package com.coffeelab.backend.model;

import java.time.LocalDateTime;

public class PublicRecipe {
    public Long id;
    public Long recipeId;
    public Long userId;
    public double averageRating;
    public int ratingCount;
    public int triedCount;
    public int favoriteCount;
    public int forkCount;
    public double hotScore;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
