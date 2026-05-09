package com.coffeelab.backend.service;

import com.coffeelab.backend.common.PageResult;
import com.coffeelab.backend.dto.CopyRecipeRequest;
import com.coffeelab.backend.dto.RecipeRequest;
import com.coffeelab.backend.exception.BusinessException;
import com.coffeelab.backend.mapper.RecipeMapper;
import com.coffeelab.backend.model.ClassicCoffee;
import com.coffeelab.backend.model.Recipe;
import com.coffeelab.backend.vo.IdVO;
import com.coffeelab.backend.vo.RecipeVO;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RecipeService {
    private final RecipeMapper recipeMapper;
    private final InMemoryStore fallbackStore;
    private final PersistenceGuard persistenceGuard;

    public RecipeService(RecipeMapper recipeMapper, InMemoryStore fallbackStore, PersistenceGuard persistenceGuard) {
        this.recipeMapper = recipeMapper;
        this.fallbackStore = fallbackStore;
        this.persistenceGuard = persistenceGuard;
    }

    public List<ClassicCoffee> classicCoffees() {
        return persistenceGuard.read(recipeMapper::selectClassicCoffees, () -> fallbackStore.classicCoffees);
    }

    public ClassicCoffee classicCoffee(Long id) {
        ClassicCoffee coffee = persistenceGuard.read(
                () -> recipeMapper.selectClassicCoffeeById(id),
                () -> fallbackStore.classicCoffees.stream()
                    .filter(item -> item.id.equals(id))
                    .findFirst()
                    .orElse(null));
        if (coffee == null) {
            throw new BusinessException(404, "classic coffee not found");
        }
        return coffee;
    }

    public Recipe classicTemplate(Long id) {
        return copyRecipe(classicCoffee(id).defaultRecipe, null, null);
    }

    public IdVO create(Long userId, RecipeRequest request) {
        Recipe recipe = fromRequest(request, new Recipe());
        recipe.userId = userId;
        persistenceGuard.write(() -> recipeMapper.insert(recipe), () -> {
            recipe.id = fallbackStore.recipeIds.getAndIncrement();
            recipe.createdAt = LocalDateTime.now();
            recipe.updatedAt = recipe.createdAt;
            fallbackStore.recipes.put(recipe.id, recipe);
        });
        return new IdVO(recipe.id);
    }

    public PageResult<RecipeVO> myRecipes(Long userId, String keyword, String tag, int page, int pageSize) {
        List<RecipeVO> records = persistenceGuard.read(
                () -> recipeMapper.selectByUser(userId, keyword, tag).stream()
                    .map(RecipeVO::from)
                    .toList(),
                () -> fallbackStore.recipes.values().stream()
                    .filter(recipe -> recipe.userId.equals(userId))
                    .filter(recipe -> !StringUtils.hasText(keyword) || recipe.name.contains(keyword))
                    .filter(recipe -> !StringUtils.hasText(tag) || recipe.flavorTags.contains(tag))
                    .sorted(Comparator.comparing((Recipe recipe) -> recipe.updatedAt).reversed())
                    .map(RecipeVO::from)
                    .toList());
        return PageResult.of(records, page, pageSize);
    }

    public RecipeVO detail(Long userId, Long id) {
        return RecipeVO.from(ownRecipe(userId, id));
    }

    public boolean update(Long userId, Long id, RecipeRequest request) {
        Recipe recipe = ownRecipe(userId, id);
        fromRequest(request, recipe);
        persistenceGuard.write(() -> recipeMapper.update(recipe), () -> {
            recipe.updatedAt = LocalDateTime.now();
            fallbackStore.recipes.put(id, recipe);
        });
        return true;
    }

    public boolean delete(Long userId, Long id) {
        ownRecipe(userId, id);
        persistenceGuard.write(() -> recipeMapper.deleteByIdAndUser(id, userId), () -> fallbackStore.recipes.remove(id));
        return true;
    }

    public IdVO copy(Long userId, Long id, CopyRecipeRequest request) {
        Recipe source = ownRecipe(userId, id);
        Recipe copy = copyRecipe(source, userId, StringUtils.hasText(request.name()) ? request.name() : source.name + " Copy");
        persistCopy(copy);
        return new IdVO(copy.id);
    }

    public IdVO copyRecipeToUser(Recipe source, Long userId, String name) {
        Recipe copy = copyRecipe(source, userId, name);
        persistCopy(copy);
        return new IdVO(copy.id);
    }

    public Recipe ownRecipe(Long userId, Long id) {
        Recipe recipe = findRecipe(id);
        if (recipe == null) {
            throw new BusinessException(404, "recipe not found");
        }
        if (!recipe.userId.equals(userId)) {
            throw new BusinessException(403, "forbidden");
        }
        return recipe;
    }

    public Recipe copyRecipe(Recipe source, Long userId, String name) {
        Recipe target = new Recipe();
        target.userId = userId;
        target.name = StringUtils.hasText(name) ? name : source.name;
        target.note = source.note;
        target.cupType = source.cupType;
        target.temperatureType = source.temperatureType;
        target.coffeeBase = source.coffeeBase;
        target.espressoShots = source.espressoShots;
        target.milkType = source.milkType;
        target.sweetness = source.sweetness;
        target.iceLevel = source.iceLevel;
        target.syrups = List.copyOf(source.syrups);
        target.foam = source.foam;
        target.toppings = List.copyOf(source.toppings);
        target.flavorTags = List.copyOf(source.flavorTags);
        target.flavorRadar = source.flavorRadar;
        target.isPublic = false;
        target.createdAt = LocalDateTime.now();
        target.updatedAt = target.createdAt;
        return target;
    }

    public Recipe findRecipe(Long id) {
        Recipe recipe = persistenceGuard.read(() -> recipeMapper.selectById(id), () -> fallbackStore.recipes.get(id));
        if (recipe == null) {
            throw new BusinessException(404, "recipe not found");
        }
        return recipe;
    }

    public void updatePublicState(Long userId, Long id, boolean isPublic) {
        persistenceGuard.write(() -> recipeMapper.updatePublicState(id, userId, isPublic), () -> {
            Recipe recipe = fallbackStore.recipes.get(id);
            if (recipe != null && recipe.userId.equals(userId)) {
                recipe.isPublic = isPublic;
                recipe.updatedAt = LocalDateTime.now();
            }
        });
    }

    private void persistCopy(Recipe copy) {
        persistenceGuard.write(() -> recipeMapper.insert(copy), () -> {
            copy.id = fallbackStore.recipeIds.getAndIncrement();
            copy.createdAt = LocalDateTime.now();
            copy.updatedAt = copy.createdAt;
            fallbackStore.recipes.put(copy.id, copy);
        });
    }

    private Recipe fromRequest(RecipeRequest request, Recipe recipe) {
        recipe.name = request.name();
        recipe.note = request.note();
        recipe.cupType = defaultValue(request.cupType(), "coldCup");
        recipe.temperatureType = defaultValue(request.temperatureType(), "cold");
        recipe.coffeeBase = defaultValue(request.coffeeBase(), "espresso");
        recipe.espressoShots = request.espressoShots() == null ? 2 : request.espressoShots();
        recipe.milkType = defaultValue(request.milkType(), "wholeMilk");
        recipe.sweetness = defaultValue(request.sweetness(), "halfSugar");
        recipe.iceLevel = defaultValue(request.iceLevel(), "normalIce");
        recipe.syrups = request.syrups() == null ? List.of() : request.syrups();
        recipe.foam = defaultValue(request.foam(), "none");
        recipe.toppings = request.toppings() == null ? List.of() : request.toppings();
        recipe.flavorTags = request.flavorTags() == null ? List.of() : request.flavorTags();
        recipe.flavorRadar = request.flavorRadar() == null ? com.coffeelab.backend.model.FlavorRadar.balanced() : request.flavorRadar();
        recipe.isPublic = request.isPublic() != null && request.isPublic();
        return recipe;
    }

    private String defaultValue(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }
}
