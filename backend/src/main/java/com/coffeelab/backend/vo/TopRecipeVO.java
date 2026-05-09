package com.coffeelab.backend.vo;

import java.util.List;

public record TopRecipeVO(
        int rank, Long publicRecipeId, String recipeName, String authorName, String cupType,
        String temperatureType, List<String> flavorTags, double averageRating, int ratingCount,
        int triedCount, int favoriteCount, int forkCount, double hotScore) {
}
