package com.coffeelab.backend.mybatis;

import com.coffeelab.backend.model.Recipe;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(Recipe.class)
public class RecipeJsonTypeHandler extends JsonTypeHandler<Recipe> {
    public RecipeJsonTypeHandler() {
        super(TypeFactory.defaultInstance().constructType(Recipe.class));
    }
}
