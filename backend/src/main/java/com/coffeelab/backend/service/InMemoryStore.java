package com.coffeelab.backend.service;

import com.coffeelab.backend.model.ClassicCoffee;
import com.coffeelab.backend.model.FlavorRadar;
import com.coffeelab.backend.model.PublicRecipe;
import com.coffeelab.backend.model.Recipe;
import com.coffeelab.backend.model.RecipeRating;
import com.coffeelab.backend.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryStore {
    public final Map<Long, User> users = new ConcurrentHashMap<>();
    public final Map<Long, Recipe> recipes = new ConcurrentHashMap<>();
    public final Map<Long, PublicRecipe> publicRecipes = new ConcurrentHashMap<>();
    public final Map<Long, RecipeRating> ratings = new ConcurrentHashMap<>();
    public final Map<Long, Set<Long>> favorites = new ConcurrentHashMap<>();
    public final Map<Long, Set<Long>> tries = new ConcurrentHashMap<>();
    public final List<ClassicCoffee> classicCoffees = new ArrayList<>();
    public final AtomicLong userIds = new AtomicLong(1);
    public final AtomicLong recipeIds = new AtomicLong(1001);
    public final AtomicLong publicRecipeIds = new AtomicLong(2001);
    public final AtomicLong ratingIds = new AtomicLong(3001);

    public InMemoryStore() {
        seedClassic("Americano", "Clean espresso with hot water", 3, "espresso", "none", "noSugar", List.of("classic", "clean"));
        seedClassic("Latte", "Espresso softened by steamed milk", 3, "espresso", "wholeMilk", "noSugar", List.of("milky", "smooth"));
        seedClassic("Cappuccino", "Balanced espresso, milk and foam", 3, "espresso", "wholeMilk", "noSugar", List.of("foam", "classic"));
        seedClassic("Mocha", "Coffee with chocolate richness", 3, "espresso", "wholeMilk", "halfSugar", List.of("chocolate", "rich"));
        seedClassic("Caramel Macchiato", "Layered milk coffee with caramel", 3, "espresso", "wholeMilk", "halfSugar", List.of("caramel", "sweet"));
        seedClassic("Cold Brew", "Slow extracted cold coffee", 4, "coldBrew", "none", "noSugar", List.of("cold", "fresh"));
        seedClassic("Flat White", "Dense espresso milk coffee", 4, "espresso", "wholeMilk", "noSugar", List.of("rich", "smooth"));

        User demo = new User();
        demo.id = userIds.getAndIncrement();
        demo.nickname = "Latte Researcher";
        demo.email = "latte@example.com";
        demo.passwordHash = "{mock}123456";
        demo.createdAt = LocalDateTime.now();
        demo.updatedAt = demo.createdAt;
        users.put(demo.id, demo);

        seedPublicRecipe(demo.id, "Midnight Hazelnut Latte", "Oat milk, espresso, caramel and hazelnut aroma.", "coldCup",
                "cold", "espresso", 2, "oatMilk", "halfSugar", "normalIce",
                List.of("hazelnut", "caramel"), "lightFoam", List.of("cocoaPowder"),
                List.of("hazelnut", "milky", "iced"), new FlavorRadar(4, 3, 1, 5, 4, 4),
                4.9, 128, 820, 214, 86);
        seedPublicRecipe(demo.id, "Sea Salt Cold Brew Cloud", "Cold brew with a sea salt cream top.", "coldCup",
                "cold", "coldBrew", 1, "none", "lowSugar", "normalIce",
                List.of("seaSaltCaramel"), "seaSaltCream", List.of("cocoaPowder"),
                List.of("coldBrew", "seaSalt", "refreshing"), new FlavorRadar(4, 2, 1, 2, 4, 5),
                4.8, 96, 640, 188, 72);
        seedPublicRecipe(demo.id, "Winter Thick Milk Mocha", "A warm mocha with thick milk and cocoa finish.", "hotCup",
                "hot", "espresso", 2, "thickMilk", "halfSugar", "noIce",
                List.of("mocha"), "thickFoam", List.of("chocolateChips"),
                List.of("mocha", "warm", "rich"), new FlavorRadar(3, 4, 1, 5, 5, 1),
                4.7, 84, 590, 162, 54);
    }

    private void seedClassic(String name, String description, int caffeineLevel, String base, String milk, String sweetness, List<String> tags) {
        Recipe template = new Recipe();
        template.name = "Classic " + name;
        template.note = "Template from classic coffee";
        template.cupType = "coldBrew".equals(base) ? "coldCup" : "hotCup";
        template.temperatureType = "coldBrew".equals(base) ? "cold" : "hot";
        template.coffeeBase = base;
        template.espressoShots = "coldBrew".equals(base) ? 0 : 2;
        template.milkType = milk;
        template.sweetness = sweetness;
        template.iceLevel = "coldBrew".equals(base) ? "normalIce" : "noIce";
        template.syrups = "caramel".equals(tags.get(0)) ? List.of("caramel") : List.of();
        template.foam = "none".equals(milk) ? "none" : "lightFoam";
        template.toppings = List.of();
        template.flavorTags = tags;
        template.flavorRadar = new FlavorRadar(3, sweetness.equals("noSugar") ? 1 : 3, 1, "none".equals(milk) ? 0 : 4, 4, "coldBrew".equals(base) ? 5 : 2);

        ClassicCoffee coffee = new ClassicCoffee();
        coffee.id = (long) classicCoffees.size() + 1;
        coffee.name = name;
        coffee.imageUrl = "/images/classic/" + name.toLowerCase().replace(" ", "-") + ".png";
        coffee.description = description;
        coffee.caffeineLevel = caffeineLevel;
        coffee.suitableCrowd = "Coffee lovers";
        coffee.tags = tags;
        coffee.defaultRecipe = template;
        coffee.adjustable = true;
        classicCoffees.add(coffee);
    }

    private void seedPublicRecipe(
            Long userId,
            String name,
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
            double averageRating,
            int ratingCount,
            int triedCount,
            int favoriteCount,
            int forkCount) {
        Recipe recipe = new Recipe();
        recipe.id = recipeIds.getAndIncrement();
        recipe.userId = userId;
        recipe.name = name;
        recipe.note = note;
        recipe.cupType = cupType;
        recipe.temperatureType = temperatureType;
        recipe.coffeeBase = coffeeBase;
        recipe.espressoShots = espressoShots;
        recipe.milkType = milkType;
        recipe.sweetness = sweetness;
        recipe.iceLevel = iceLevel;
        recipe.syrups = syrups;
        recipe.foam = foam;
        recipe.toppings = toppings;
        recipe.flavorTags = flavorTags;
        recipe.flavorRadar = flavorRadar;
        recipe.isPublic = true;
        recipe.createdAt = LocalDateTime.now();
        recipe.updatedAt = recipe.createdAt;
        recipes.put(recipe.id, recipe);

        PublicRecipe publicRecipe = new PublicRecipe();
        publicRecipe.id = publicRecipeIds.getAndIncrement();
        publicRecipe.recipeId = recipe.id;
        publicRecipe.userId = userId;
        publicRecipe.averageRating = averageRating;
        publicRecipe.ratingCount = ratingCount;
        publicRecipe.triedCount = triedCount;
        publicRecipe.favoriteCount = favoriteCount;
        publicRecipe.forkCount = forkCount;
        publicRecipe.hotScore = averageRating * 40 + ratingCount * 2 + triedCount * 1.5 + favoriteCount * 2 + forkCount * 2;
        publicRecipe.createdAt = recipe.createdAt;
        publicRecipe.updatedAt = recipe.updatedAt;
        publicRecipes.put(publicRecipe.id, publicRecipe);
    }

    public Map<Integer, Integer> ratingDistribution(Long publicRecipeId) {
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            result.put(i, 0);
        }
        ratings.values().stream()
                .filter(rating -> rating.publicRecipeId.equals(publicRecipeId))
                .forEach(rating -> result.compute(rating.score, (score, count) -> count == null ? 1 : count + 1));
        return result;
    }

    public Set<Long> favoriteUsers(Long publicRecipeId) {
        return favorites.computeIfAbsent(publicRecipeId, id -> ConcurrentHashMap.newKeySet());
    }

    public Set<Long> tryUsers(Long publicRecipeId) {
        return tries.computeIfAbsent(publicRecipeId, id -> new HashSet<>());
    }
}
