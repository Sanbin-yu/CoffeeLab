package com.coffeelab.backend.controller;

import com.coffeelab.backend.common.ApiResponse;
import com.coffeelab.backend.model.ClassicCoffee;
import com.coffeelab.backend.model.Recipe;
import com.coffeelab.backend.service.RecipeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classic-coffees")
public class ClassicCoffeeController {
    private final RecipeService recipeService;

    public ClassicCoffeeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ApiResponse<List<ClassicCoffee>> list() {
        return ApiResponse.success(recipeService.classicCoffees());
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassicCoffee> detail(@PathVariable Long id) {
        return ApiResponse.success(recipeService.classicCoffee(id));
    }

    @GetMapping("/{id}/recipe-template")
    public ApiResponse<Recipe> template(@PathVariable Long id) {
        return ApiResponse.success(recipeService.classicTemplate(id));
    }
}
