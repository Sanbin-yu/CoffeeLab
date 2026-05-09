package com.coffeelab.backend.controller;

import com.coffeelab.backend.common.ApiResponse;
import com.coffeelab.backend.service.PublicRecipeService;
import com.coffeelab.backend.vo.TopRecipeVO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rankings")
public class RankingController {
    private final PublicRecipeService service;

    public RankingController(PublicRecipeService service) {
        this.service = service;
    }

    @GetMapping("/top-recipes")
    public ApiResponse<List<TopRecipeVO>> topRecipes(@RequestParam(defaultValue = "all") String range) {
        return ApiResponse.success(service.topRecipes(range));
    }
}
