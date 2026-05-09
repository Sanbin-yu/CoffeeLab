package com.coffeelab.backend.controller;

import com.coffeelab.backend.common.ApiResponse;
import com.coffeelab.backend.config.AuthContext;
import com.coffeelab.backend.dto.UserUpdateRequest;
import com.coffeelab.backend.service.UserService;
import com.coffeelab.backend.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(userService.me(AuthContext.getUserId()));
    }

    @PutMapping("/me")
    public ApiResponse<UserVO> update(@RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.update(AuthContext.getUserId(), request));
    }
}
