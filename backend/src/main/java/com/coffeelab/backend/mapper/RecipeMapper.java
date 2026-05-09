package com.coffeelab.backend.mapper;

import com.coffeelab.backend.model.ClassicCoffee;
import com.coffeelab.backend.model.Recipe;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecipeMapper {
    List<ClassicCoffee> selectClassicCoffees();

    ClassicCoffee selectClassicCoffeeById(Long id);

    Recipe selectById(Long id);

    List<Recipe> selectByUser(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("tag") String tag);

    int insert(Recipe recipe);

    int update(Recipe recipe);

    int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);

    int updatePublicState(@Param("id") Long id, @Param("userId") Long userId, @Param("isPublic") boolean isPublic);
}
