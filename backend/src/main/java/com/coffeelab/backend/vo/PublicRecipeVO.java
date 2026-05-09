package com.coffeelab.backend.vo;

import com.coffeelab.backend.model.FlavorRadar;
import java.time.LocalDateTime;
import java.util.List;

public record PublicRecipeVO(
        Long id, Long recipeId, String name, String note, Long authorId, String authorName,
        String cupType, String temperatureType, String coffeeBase, Integer espressoShots, String milkType,
        String sweetness, String iceLevel, List<String> syrups, String foam, List<String> toppings,
        List<String> flavorTags, FlavorRadar flavorRadar, double averageRating, int ratingCount,
        int triedCount, int favoriteCount, int forkCount, double hotScore, LocalDateTime createdAt) {
}
