package com.coffeelab.backend.model;

public record FlavorRadar(int bitterness, int sweetness, int acidity, int milkiness, int richness, int freshness) {
    public static FlavorRadar balanced() {
        return new FlavorRadar(3, 3, 2, 3, 3, 3);
    }
}
