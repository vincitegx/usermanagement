package com.davidogbodu.usermanagement.user;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserMapper implements Function<Users, UserDto> {
    @Override
    public UserDto apply(Users user) {
        return UserDto.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
