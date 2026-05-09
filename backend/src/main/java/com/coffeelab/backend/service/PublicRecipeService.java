package com.coffeelab.backend.service;

import com.coffeelab.backend.common.PageResult;
import com.coffeelab.backend.dto.CopyRecipeRequest;
import com.coffeelab.backend.dto.RatingRequest;
import com.coffeelab.backend.exception.BusinessException;
import com.coffeelab.backend.mapper.PublicRecipeMapper;
import com.coffeelab.backend.model.PublicRecipe;
import com.coffeelab.backend.model.Recipe;
import com.coffeelab.backend.model.RecipeRating;
import com.coffeelab.backend.vo.CountVO;
import com.coffeelab.backend.vo.IdVO;
import com.coffeelab.backend.vo.PublicRecipeVO;
import com.coffeelab.backend.vo.RatingBucketVO;
import com.coffeelab.backend.vo.RatingSummaryVO;
import com.coffeelab.backend.vo.TopRecipeVO;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PublicRecipeService {
    private final PublicRecipeMapper publicRecipeMapper;
    private final InMemoryStore fallbackStore;
    private final PersistenceGuard persistenceGuard;
    private final RecipeService recipeService;
    private final UserService userService;

    public PublicRecipeService(PublicRecipeMapper publicRecipeMapper, InMemoryStore fallbackStore, PersistenceGuard persistenceGuard, RecipeService recipeService, UserService userService) {
        this.publicRecipeMapper = publicRecipeMapper;
        this.fallbackStore = fallbackStore;
        this.persistenceGuard = persistenceGuard;
        this.recipeService = recipeService;
        this.userService = userService;
    }

    public Map<String, Long> publish(Long userId, Long recipeId) {
        Recipe recipe = recipeService.ownRecipe(userId, recipeId);
        PublicRecipe existing = persistenceGuard.read(
                () -> publicRecipeMapper.selectByRecipeId(recipeId),
                () -> fallbackStore.publicRecipes.values().stream()
                    .filter(publicRecipe -> publicRecipe.recipeId.equals(recipeId))
                    .findFirst()
                    .orElse(null));
        if (existing != null) {
            return Map.of("publicRecipeId", existing.id);
        }
        PublicRecipe publicRecipe = new PublicRecipe();
        publicRecipe.recipeId = recipeId;
        publicRecipe.userId = userId;
        persistenceGuard.write(() -> publicRecipeMapper.insert(publicRecipe), () -> {
            publicRecipe.id = fallbackStore.publicRecipeIds.getAndIncrement();
            publicRecipe.createdAt = LocalDateTime.now();
            publicRecipe.updatedAt = publicRecipe.createdAt;
            fallbackStore.publicRecipes.put(publicRecipe.id, publicRecipe);
        });
        recipeService.updatePublicState(userId, recipe.id, true);
        return Map.of("publicRecipeId", publicRecipe.id);
    }

    public boolean unpublish(Long userId, Long recipeId) {
        recipeService.ownRecipe(userId, recipeId);
        persistenceGuard.write(
                () -> publicRecipeMapper.deleteByRecipeIdAndUser(recipeId, userId),
                () -> fallbackStore.publicRecipes.values().removeIf(publicRecipe ->
                        publicRecipe.recipeId.equals(recipeId) && publicRecipe.userId.equals(userId)));
        recipeService.updatePublicState(userId, recipeId, false);
        return true;
    }

    public PageResult<PublicRecipeVO> list(String keyword, String tag, String sort, int page, int pageSize) {
        List<PublicRecipeVO> records = persistenceGuard.read(
                () -> publicRecipeMapper.selectPublicRecipeViews(keyword, tag, StringUtils.hasText(sort) ? sort : "latest"),
                () -> fallbackStore.publicRecipes.values().stream()
                    .map(this::toFallbackVO)
                    .filter(vo -> !StringUtils.hasText(keyword) || vo.name().contains(keyword))
                    .filter(vo -> !StringUtils.hasText(tag) || vo.flavorTags().contains(tag))
                    .sorted(fallbackComparator(sort))
                    .toList());
        return PageResult.of(records, page, pageSize);
    }

    public PublicRecipeVO detail(Long publicRecipeId) {
        PublicRecipeVO vo = persistenceGuard.read(
                () -> publicRecipeMapper.selectPublicRecipeViewById(publicRecipeId),
                () -> {
            PublicRecipe publicRecipe = fallbackStore.publicRecipes.get(publicRecipeId);
            return publicRecipe == null ? null : toFallbackVO(publicRecipe);
        });
        if (vo == null) {
            throw new BusinessException(404, "public recipe not found");
        }
        return vo;
    }

    public Map<String, Object> rate(Long userId, Long publicRecipeId, RatingRequest request) {
        PublicRecipe publicRecipe = findPublic(publicRecipeId);
        RecipeRating rating = new RecipeRating();
        rating.publicRecipeId = publicRecipeId;
        rating.userId = userId;
        rating.score = request.score();
        rating.comment = request.comment();
        persistenceGuard.write(() -> {
            publicRecipeMapper.upsertRating(rating);
            publicRecipeMapper.recalculateStats(publicRecipeId);
        }, () -> fallbackRate(publicRecipeId, userId, request));
        PublicRecipe updated = findPublic(publicRecipe.id);
        return Map.of("averageRating", updated.averageRating, "ratingCount", updated.ratingCount);
    }

    public RatingSummaryVO ratingSummary(Long publicRecipeId) {
        PublicRecipe publicRecipe = findPublic(publicRecipeId);
        Map<Integer, Integer> distribution = new java.util.HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0);
        }
        List<RatingBucketVO> buckets = persistenceGuard.read(
                () -> publicRecipeMapper.selectRatingDistribution(publicRecipeId),
                () -> fallbackStore.ratingDistribution(publicRecipeId).entrySet().stream()
                        .map(entry -> new RatingBucketVO(entry.getKey(), entry.getValue()))
                        .toList());
        for (RatingBucketVO bucket : buckets) {
                distribution.put(bucket.score(), bucket.count());
        }
        return new RatingSummaryVO(publicRecipe.averageRating, publicRecipe.ratingCount, distribution);
    }

    public CountVO tryRecipe(Long userId, Long publicRecipeId) {
        PublicRecipe publicRecipe = findPublic(publicRecipeId);
        persistenceGuard.write(() -> {
            publicRecipeMapper.insertTryRecord(publicRecipeId, userId);
            publicRecipeMapper.recalculateStats(publicRecipeId);
        }, () -> {
            if (fallbackStore.tryUsers(publicRecipeId).add(userId)) {
                publicRecipe.triedCount++;
                recalculateFallback(publicRecipe);
            }
        });
        PublicRecipe updated = findPublic(publicRecipe.id);
        return new CountVO(updated.triedCount, null, null, null, null);
    }

    public CountVO favorite(Long userId, Long publicRecipeId) {
        PublicRecipe publicRecipe = findPublic(publicRecipeId);
        persistenceGuard.write(() -> {
            publicRecipeMapper.insertFavorite(publicRecipeId, userId);
            publicRecipeMapper.recalculateStats(publicRecipeId);
        }, () -> {
            if (fallbackStore.favoriteUsers(publicRecipeId).add(userId)) {
                publicRecipe.favoriteCount++;
                recalculateFallback(publicRecipe);
            }
        });
        PublicRecipe updated = findPublic(publicRecipe.id);
        return new CountVO(null, updated.favoriteCount, null, null, null);
    }

    public CountVO unfavorite(Long userId, Long publicRecipeId) {
        PublicRecipe publicRecipe = findPublic(publicRecipeId);
        persistenceGuard.write(() -> {
            publicRecipeMapper.deleteFavorite(publicRecipeId, userId);
            publicRecipeMapper.recalculateStats(publicRecipeId);
        }, () -> {
            if (fallbackStore.favoriteUsers(publicRecipeId).remove(userId)) {
                publicRecipe.favoriteCount = Math.max(0, publicRecipe.favoriteCount - 1);
                recalculateFallback(publicRecipe);
            }
        });
        PublicRecipe updated = findPublic(publicRecipe.id);
        return new CountVO(null, updated.favoriteCount, null, null, null);
    }

    public CountVO fork(Long userId, Long publicRecipeId, CopyRecipeRequest request) {
        PublicRecipe publicRecipe = findPublic(publicRecipeId);
        Recipe source = recipeService.findRecipe(publicRecipe.recipeId);
        IdVO copied = recipeService.copyRecipeToUser(source, userId, StringUtils.hasText(request.name()) ? request.name() : source.name + " Fork");
        persistenceGuard.write(() -> {
            publicRecipeMapper.insertForkRecord(publicRecipeId, userId, source.id, copied.id());
            publicRecipeMapper.incrementForkCount(publicRecipeId);
        }, () -> {
            publicRecipe.forkCount++;
            recalculateFallback(publicRecipe);
        });
        PublicRecipe updated = findPublic(publicRecipe.id);
        return new CountVO(null, null, updated.forkCount, copied.id(), null);
    }

    public List<TopRecipeVO> topRecipes(String range) {
        List<PublicRecipe> sorted = persistenceGuard.read(
                publicRecipeMapper::selectTopPublicRecipes,
                () -> fallbackStore.publicRecipes.values().stream()
                    .sorted(Comparator.comparingDouble((PublicRecipe publicRecipe) -> publicRecipe.hotScore).reversed())
                    .limit(20)
                    .toList());
        final int[] rank = {0};
        return sorted.stream().map(publicRecipe -> {
            PublicRecipeVO vo = detail(publicRecipe.id);
            rank[0]++;
            return new TopRecipeVO(rank[0], vo.id(), vo.name(), vo.authorName(), vo.cupType(), vo.temperatureType(),
                    vo.flavorTags(), vo.averageRating(), vo.ratingCount(), vo.triedCount(),
                    vo.favoriteCount(), vo.forkCount(), vo.hotScore());
        }).toList();
    }

    private PublicRecipe findPublic(Long publicRecipeId) {
        PublicRecipe publicRecipe = persistenceGuard.read(() -> publicRecipeMapper.selectById(publicRecipeId), () -> fallbackStore.publicRecipes.get(publicRecipeId));
        if (publicRecipe == null) {
            throw new BusinessException(404, "public recipe not found");
        }
        return publicRecipe;
    }

    private PublicRecipeVO toFallbackVO(PublicRecipe publicRecipe) {
        Recipe recipe = fallbackStore.recipes.get(publicRecipe.recipeId);
        com.coffeelab.backend.model.User author = userService.find(publicRecipe.userId);
        return new PublicRecipeVO(publicRecipe.id, recipe.id, recipe.name, recipe.note, author.id, author.nickname,
                recipe.cupType, recipe.temperatureType, recipe.coffeeBase, recipe.espressoShots, recipe.milkType,
                recipe.sweetness, recipe.iceLevel, recipe.syrups, recipe.foam, recipe.toppings, recipe.flavorTags,
                recipe.flavorRadar, publicRecipe.averageRating, publicRecipe.ratingCount, publicRecipe.triedCount,
                publicRecipe.favoriteCount, publicRecipe.forkCount, publicRecipe.hotScore, publicRecipe.createdAt);
    }

    private Comparator<PublicRecipeVO> fallbackComparator(String sort) {
        return switch (StringUtils.hasText(sort) ? sort : "latest") {
            case "rating" -> Comparator.comparingDouble(PublicRecipeVO::averageRating).reversed();
            case "tried" -> Comparator.comparingInt(PublicRecipeVO::triedCount).reversed();
            case "favorite" -> Comparator.comparingInt(PublicRecipeVO::favoriteCount).reversed();
            case "hot" -> Comparator.comparingDouble(PublicRecipeVO::hotScore).reversed();
            default -> Comparator.comparing(PublicRecipeVO::createdAt).reversed();
        };
    }

    private void fallbackRate(Long publicRecipeId, Long userId, RatingRequest request) {
        RecipeRating rating = fallbackStore.ratings.values().stream()
                .filter(item -> item.publicRecipeId.equals(publicRecipeId) && item.userId.equals(userId))
                .findFirst()
                .orElseGet(() -> {
                    RecipeRating item = new RecipeRating();
                    item.id = fallbackStore.ratingIds.getAndIncrement();
                    item.publicRecipeId = publicRecipeId;
                    item.userId = userId;
                    item.createdAt = LocalDateTime.now();
                    fallbackStore.ratings.put(item.id, item);
                    return item;
                });
        rating.score = request.score();
        rating.comment = request.comment();
        rating.updatedAt = LocalDateTime.now();
        recalculateFallback(fallbackStore.publicRecipes.get(publicRecipeId));
    }

    private void recalculateFallback(PublicRecipe publicRecipe) {
        List<RecipeRating> recipeRatings = fallbackStore.ratings.values().stream()
                .filter(rating -> rating.publicRecipeId.equals(publicRecipe.id))
                .toList();
        publicRecipe.ratingCount = recipeRatings.size();
        publicRecipe.averageRating = recipeRatings.isEmpty()
                ? 0
                : Math.round(recipeRatings.stream().mapToInt(rating -> rating.score).average().orElse(0) * 10.0) / 10.0;
        publicRecipe.hotScore = publicRecipe.averageRating * 40
                + publicRecipe.ratingCount * 2
                + publicRecipe.triedCount * 1.5
                + publicRecipe.favoriteCount * 2
                + publicRecipe.forkCount * 2;
        publicRecipe.updatedAt = LocalDateTime.now();
    }
}
