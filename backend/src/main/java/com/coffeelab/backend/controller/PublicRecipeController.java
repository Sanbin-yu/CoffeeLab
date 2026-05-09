package com.coffeelab.backend.controller;

import com.coffeelab.backend.common.ApiResponse;
import com.coffeelab.backend.common.PageResult;
import com.coffeelab.backend.config.AuthContext;
import com.coffeelab.backend.dto.CopyRecipeRequest;
import com.coffeelab.backend.dto.RatingRequest;
import com.coffeelab.backend.service.PublicRecipeService;
import com.coffeelab.backend.vo.CountVO;
import com.coffeelab.backend.vo.PublicRecipeVO;
import com.coffeelab.backend.vo.RatingSummaryVO;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public-recipes")
public class PublicRecipeController {
    private final PublicRecipeService service;

    public PublicRecipeController(PublicRecipeService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<PublicRecipeVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize) {
        return ApiResponse.success(service.list(keyword, tag, sort, page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<PublicRecipeVO> detail(@PathVariable Long id) {
        return ApiResponse.success(service.detail(id));
    }

    @PostMapping("/{id}/ratings")
    public ApiResponse<Map<String, Object>> rate(@PathVariable Long id, @Valid @RequestBody RatingRequest request) {
        return ApiResponse.success(service.rate(AuthContext.getUserId(), id, request));
    }

    @GetMapping("/{id}/ratings/summary")
    public ApiResponse<RatingSummaryVO> ratingSummary(@PathVariable Long id) {
        return ApiResponse.success(service.ratingSummary(id));
    }

    @PostMapping("/{id}/try")
    public ApiResponse<CountVO> tryRecipe(@PathVariable Long id) {
        return ApiResponse.success(service.tryRecipe(AuthContext.getUserId(), id));
    }

    @PostMapping("/{id}/favorite")
    public ApiResponse<CountVO> favorite(@PathVariable Long id) {
        return ApiResponse.success(service.favorite(AuthContext.getUserId(), id));
    }

    @DeleteMapping("/{id}/favorite")
    public ApiResponse<CountVO> unfavorite(@PathVariable Long id) {
        return ApiResponse.success(service.unfavorite(AuthContext.getUserId(), id));
    }

    @PostMapping("/{id}/fork")
    public ApiResponse<CountVO> fork(@PathVariable Long id, @RequestBody(required = false) CopyRecipeRequest request) {
        CopyRecipeRequest safeRequest = request == null ? new CopyRecipeRequest(null) : request;
        return ApiResponse.success(service.fork(AuthContext.getUserId(), id, safeRequest));
    }
}
