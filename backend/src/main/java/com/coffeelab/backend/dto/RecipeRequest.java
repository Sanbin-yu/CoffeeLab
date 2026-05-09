package com.coffeelab.backend.dto;

import com.coffeelab.backend.model.FlavorRadar;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RecipeRequest(
        @NotBlank String name,
        String note,
        String cupType,
        String temperatureType,
        String coffeeBase,
        Integer espressoShots,
        String milkType,
        String sweetness,
        String iceLevel,
        List<String> syrups,
        String foam,
        List<String> toppings,
        List<String> flavorTags,
        FlavorRadar flavorRadar,
        Boolean isPublic) {
}
