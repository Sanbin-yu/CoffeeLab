package com.coffeelab.backend.mapper;

import com.coffeelab.backend.model.PublicRecipe;
import com.coffeelab.backend.model.RecipeRating;
import com.coffeelab.backend.vo.RatingBucketVO;
import com.coffeelab.backend.vo.PublicRecipeVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PublicRecipeMapper {
    PublicRecipe selectById(Long id);

    PublicRecipe selectByRecipeId(Long recipeId);

    List<PublicRecipeVO> selectPublicRecipeViews(
            @Param("keyword") String keyword,
            @Param("tag") String tag,
            @Param("sort") String sort);

    PublicRecipeVO selectPublicRecipeViewById(Long id);

    List<PublicRecipe> selectTopPublicRecipes();

    int insert(PublicRecipe publicRecipe);

    int deleteByRecipeIdAndUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    int upsertRating(RecipeRating rating);

    List<RecipeRating> selectRatings(Long publicRecipeId);

    RecipeRating selectRatingByUser(@Param("publicRecipeId") Long publicRecipeId, @Param("userId") Long userId);

    List<RatingBucketVO> selectRatingDistribution(Long publicRecipeId);

    int insertTryRecord(@Param("publicRecipeId") Long publicRecipeId, @Param("userId") Long userId);

    int insertFavorite(@Param("publicRecipeId") Long publicRecipeId, @Param("userId") Long userId);

    int deleteFavorite(@Param("publicRecipeId") Long publicRecipeId, @Param("userId") Long userId);

    int insertForkRecord(
            @Param("publicRecipeId") Long publicRecipeId,
            @Param("userId") Long userId,
            @Param("sourceRecipeId") Long sourceRecipeId,
            @Param("forkedRecipeId") Long forkedRecipeId);

    int recalculateStats(Long publicRecipeId);

    int incrementForkCount(Long publicRecipeId);
}
