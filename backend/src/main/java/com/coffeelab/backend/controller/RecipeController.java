package com.coffeelab.backend.controller;

import com.coffeelab.backend.common.ApiResponse;
import com.coffeelab.backend.common.PageResult;
import com.coffeelab.backend.config.AuthContext;
import com.coffeelab.backend.dto.CopyRecipeRequest;
import com.coffeelab.backend.dto.RecipeRequest;
import com.coffeelab.backend.service.PublicRecipeService;
import com.coffeelab.backend.service.RecipeService;
import com.coffeelab.backend.vo.IdVO;
import com.coffeelab.backend.vo.RecipeVO;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final PublicRecipeService publicRecipeService;

    public RecipeController(RecipeService recipeService, PublicRecipeService publicRecipeService) {
        this.recipeService = recipeService;
        this.publicRecipeService = publicRecipeService;
    }

    @PostMapping
    public ApiResponse<IdVO> create(@Valid @RequestBody RecipeRequest request) {
        return ApiResponse.success(recipeService.create(AuthContext.getUserId(), request));
    }

    @GetMapping("/my")
    public ApiResponse<PageResult<RecipeVO>> myRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize) {
        return ApiResponse.success(recipeService.myRecipes(AuthContext.getUserId(), keyword, tag, page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<RecipeVO> detail(@PathVariable Long id) {
        return ApiResponse.success(recipeService.detail(AuthContext.getUserId(), id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @Valid @RequestBody RecipeRequest request) {
        return ApiResponse.success(recipeService.update(AuthContext.getUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.success(recipeService.delete(AuthContext.getUserId(), id));
    }

    @PostMapping("/{id}/copy")
    public ApiResponse<IdVO> copy(@PathVariable Long id, @RequestBody(required = false) CopyRecipeRequest request) {
        CopyRecipeRequest safeRequest = request == null ? new CopyRecipeRequest(null) : request;
        return ApiResponse.success(recipeService.copy(AuthContext.getUserId(), id, safeRequest));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Map<String, Long>> publish(@PathVariable Long id) {
        return ApiResponse.success(publicRecipeService.publish(AuthContext.getUserId(), id));
    }

    @PostMapping("/{id}/unpublish")
    public ApiResponse<Boolean> unpublish(@PathVariable Long id) {
        return ApiResponse.success(publicRecipeService.unpublish(AuthContext.getUserId(), id));
    }
}
