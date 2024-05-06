package com.davidogbodu.usermanagement.user.auth;

import com.davidogbodu.usermanagement.user.UserDto;
import lombok.Data;

@Data
public class JwtResponse {
    private String authToken;
    private UserDto user;

    public JwtResponse(String authToken, UserDto user){
        this.authToken = authToken;
        this.user = user;
    }
}
