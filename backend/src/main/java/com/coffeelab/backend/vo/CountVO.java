package com.coffeelab.backend.vo;

public record CountVO(Integer triedCount, Integer favoriteCount, Integer forkCount, Long recipeId, Long publicRecipeId) {
}
