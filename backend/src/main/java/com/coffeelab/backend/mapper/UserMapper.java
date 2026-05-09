package com.coffeelab.backend.mapper;

import com.coffeelab.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(Long id);

    User selectByAccount(@Param("account") String account);

    int countByContact(@Param("phone") String phone, @Param("email") String email);

    int insert(User user);

    int updateProfile(User user);
}
