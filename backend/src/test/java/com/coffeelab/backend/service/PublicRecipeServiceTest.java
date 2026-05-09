package com.coffeelab.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import com.coffeelab.backend.dto.CopyRecipeRequest;
import com.coffeelab.backend.dto.RatingRequest;
import com.coffeelab.backend.mapper.PublicRecipeMapper;
import com.coffeelab.backend.mapper.RecipeMapper;
import com.coffeelab.backend.model.PublicRecipe;
import com.coffeelab.backend.vo.CountVO;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicRecipeServiceTest {
    private InMemoryStore store;
    private PublicRecipeService service;
    private Long publicRecipeId;
    private Long sourceRecipeId;

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
        PublicRecipe publicRecipe = store.publicRecipes.values().iterator().next();
        publicRecipeId = publicRecipe.id;
        sourceRecipeId = publicRecipe.recipeId;
        forceFallbackCounters(publicRecipe);

        PublicRecipeMapper publicRecipeMapper = mock(PublicRecipeMapper.class);
        RecipeMapper recipeMapper = mock(RecipeMapper.class);
        RuntimeException databaseUnavailable = new RuntimeException("database unavailable in unit test");

        doThrow(databaseUnavailable).when(publicRecipeMapper).upsertRating(any());
        doThrow(databaseUnavailable).when(publicRecipeMapper).selectById(anyLong());
        doThrow(databaseUnavailable).when(publicRecipeMapper).insertTryRecord(anyLong(), anyLong());
        doThrow(databaseUnavailable).when(publicRecipeMapper).insertFavorite(anyLong(), anyLong());
        doThrow(databaseUnavailable).when(publicRecipeMapper).deleteFavorite(anyLong(), anyLong());
        doThrow(databaseUnavailable).when(publicRecipeMapper).insertForkRecord(anyLong(), anyLong(), anyLong(), anyLong());

        doAnswer(invocation -> {
            com.coffeelab.backend.model.Recipe recipe = invocation.getArgument(0);
            recipe.id = store.recipeIds.getAndIncrement();
            store.recipes.put(recipe.id, recipe);
            return 1;
        }).when(recipeMapper).insert(any());

        PersistenceGuard persistenceGuard = new PersistenceGuard();
        RecipeService recipeService = new RecipeService(recipeMapper, store, persistenceGuard);
        UserService userService = mock(UserService.class);
        service = new PublicRecipeService(publicRecipeMapper, store, persistenceGuard, recipeService, userService);
    }

    @Test
    void rateCreatesFirstRatingThenUpdatesExistingUserRatingWithoutIncreasingCount() {
        Map<String, Object> firstRating = service.rate(42L, publicRecipeId, new RatingRequest(5, "great"));
        Map<String, Object> changedRating = service.rate(42L, publicRecipeId, new RatingRequest(3, "changed"));

        assertThat(firstRating).containsEntry("ratingCount", 1).containsEntry("averageRating", 5.0);
        assertThat(changedRating).containsEntry("ratingCount", 1).containsEntry("averageRating", 3.0);
        assertThat(store.ratings.values())
                .singleElement()
                .satisfies(rating -> {
                    assertThat(rating.userId).isEqualTo(42L);
                    assertThat(rating.score).isEqualTo(3);
                });
    }

    @Test
    void tryRecipeCountsEachUserOnce() {
        CountVO firstTry = service.tryRecipe(42L, publicRecipeId);
        CountVO duplicateTry = service.tryRecipe(42L, publicRecipeId);
        CountVO anotherUserTry = service.tryRecipe(43L, publicRecipeId);

        assertThat(firstTry.triedCount()).isEqualTo(1);
        assertThat(duplicateTry.triedCount()).isEqualTo(1);
        assertThat(anotherUserTry.triedCount()).isEqualTo(2);
    }

    @Test
    void favoriteAndUnfavoriteKeepCountIdempotentAndNonNegative() {
        CountVO firstFavorite = service.favorite(42L, publicRecipeId);
        CountVO duplicateFavorite = service.favorite(42L, publicRecipeId);
        CountVO removedFavorite = service.unfavorite(42L, publicRecipeId);
        CountVO duplicateRemove = service.unfavorite(42L, publicRecipeId);

        assertThat(firstFavorite.favoriteCount()).isEqualTo(1);
        assertThat(duplicateFavorite.favoriteCount()).isEqualTo(1);
        assertThat(removedFavorite.favoriteCount()).isZero();
        assertThat(duplicateRemove.favoriteCount()).isZero();
    }

    @Test
    void forkCopiesRecipeForUserAndIncrementsForkCount() {
        CountVO fork = service.fork(42L, publicRecipeId, new CopyRecipeRequest("My fork"));

        assertThat(fork.forkCount()).isEqualTo(1);
        assertThat(fork.recipeId()).isNotNull();
        assertThat(fork.recipeId()).isNotEqualTo(sourceRecipeId);
        assertThat(store.recipes.get(fork.recipeId()).userId).isEqualTo(42L);
        assertThat(store.recipes.get(fork.recipeId()).name).isEqualTo("My fork");
    }

    private void forceFallbackCounters(PublicRecipe publicRecipe) {
        publicRecipe.averageRating = 0;
        publicRecipe.ratingCount = 0;
        publicRecipe.triedCount = 0;
        publicRecipe.favoriteCount = 0;
        publicRecipe.forkCount = 0;
        publicRecipe.hotScore = 0;
    }
}
