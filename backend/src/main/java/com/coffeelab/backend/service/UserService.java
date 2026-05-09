package com.coffeelab.backend.service;

import com.coffeelab.backend.config.TokenService;
import com.coffeelab.backend.dto.LoginRequest;
import com.coffeelab.backend.dto.RegisterRequest;
import com.coffeelab.backend.dto.UserUpdateRequest;
import com.coffeelab.backend.exception.BusinessException;
import com.coffeelab.backend.mapper.UserMapper;
import com.coffeelab.backend.model.User;
import com.coffeelab.backend.vo.LoginVO;
import com.coffeelab.backend.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final InMemoryStore fallbackStore;
    private final PersistenceGuard persistenceGuard;
    private final TokenService tokenService;

    public UserService(UserMapper userMapper, InMemoryStore fallbackStore, PersistenceGuard persistenceGuard, TokenService tokenService) {
        this.userMapper = userMapper;
        this.fallbackStore = fallbackStore;
        this.persistenceGuard = persistenceGuard;
        this.tokenService = tokenService;
    }

    public UserVO register(RegisterRequest request) {
        if (!StringUtils.hasText(request.nickname())) {
            throw new BusinessException(400, "nickname is required");
        }
        if (!StringUtils.hasText(request.phone()) && !StringUtils.hasText(request.email())) {
            throw new BusinessException(400, "phone or email is required");
        }
        return persistenceGuard.read(() -> {
            if (userMapper.countByContact(request.phone(), request.email()) > 0) {
                throw new BusinessException(409, "account already exists");
            }
            User user = new User();
            user.nickname = request.nickname();
            user.phone = request.phone();
            user.email = request.email();
            user.passwordHash = "{mock}" + request.password();
            userMapper.insert(user);
            return UserVO.from(user);
        }, () -> registerFallback(request));
    }

    public LoginVO login(LoginRequest request) {
        return persistenceGuard.read(() -> {
            User user = userMapper.selectByAccount(request.account());
            if (user == null) {
                throw new BusinessException(401, "unauthorized");
            }
            if (!user.passwordHash.equals("{mock}" + request.password())) {
                throw new BusinessException(401, "unauthorized");
            }
            return new LoginVO(tokenService.issue(user.id), UserVO.from(user));
        }, () -> {
            User user = fallbackStore.users.values().stream()
                    .filter(candidate -> request.account().equals(candidate.phone) || request.account().equals(candidate.email))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(401, "unauthorized"));
            if (!user.passwordHash.equals("{mock}" + request.password())) {
                throw new BusinessException(401, "unauthorized");
            }
            return new LoginVO(tokenService.issue(user.id), UserVO.from(user));
        });
    }

    public UserVO me(Long userId) {
        return UserVO.from(find(userId));
    }

    public UserVO update(Long userId, UserUpdateRequest request) {
        User user = find(userId);
        if (StringUtils.hasText(request.nickname())) {
            user.nickname = request.nickname();
        }
        user.avatarUrl = request.avatarUrl();
        persistenceGuard.write(() -> userMapper.updateProfile(user), () -> {
            user.updatedAt = java.time.LocalDateTime.now();
            fallbackStore.users.put(user.id, user);
        });
        return UserVO.from(user);
    }

    public User find(Long userId) {
        User user = persistenceGuard.read(() -> userMapper.selectById(userId), () -> fallbackStore.users.get(userId));
        if (user == null) {
            throw new BusinessException(404, "user not found");
        }
        return user;
    }

    private UserVO registerFallback(RegisterRequest request) {
        boolean exists = fallbackStore.users.values().stream().anyMatch(user ->
                StringUtils.hasText(request.phone()) && request.phone().equals(user.phone)
                        || StringUtils.hasText(request.email()) && request.email().equals(user.email));
        if (exists) {
            throw new BusinessException(409, "account already exists");
        }
        User user = new User();
        user.id = fallbackStore.userIds.getAndIncrement();
        user.nickname = request.nickname();
        user.phone = request.phone();
        user.email = request.email();
        user.passwordHash = "{mock}" + request.password();
        user.createdAt = java.time.LocalDateTime.now();
        user.updatedAt = user.createdAt;
        fallbackStore.users.put(user.id, user);
        return UserVO.from(user);
    }
}
