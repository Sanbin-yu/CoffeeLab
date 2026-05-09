package com.coffeelab.backend.vo;

import com.coffeelab.backend.model.FlavorRadar;
import com.coffeelab.backend.model.Recipe;
import java.time.LocalDateTime;
import java.util.List;

public record RecipeVO(
        Long id, Long userId, String name, String note, String cupType, String temperatureType,
        String coffeeBase, Integer espressoShots, String milkType, String sweetness, String iceLevel,
        List<String> syrups, String foam, List<String> toppings, List<String> flavorTags,
        FlavorRadar flavorRadar, Boolean isPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static RecipeVO from(Recipe recipe) {
        return new RecipeVO(recipe.id, recipe.userId, recipe.name, recipe.note, recipe.cupType,
                recipe.temperatureType, recipe.coffeeBase, recipe.espressoShots, recipe.milkType,
                recipe.sweetness, recipe.iceLevel, recipe.syrups, recipe.foam, recipe.toppings,
                recipe.flavorTags, recipe.flavorRadar, recipe.isPublic, recipe.createdAt, recipe.updatedAt);
    }
}
