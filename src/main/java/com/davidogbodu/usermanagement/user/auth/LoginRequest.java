package com.davidogbodu.usermanagement.user.auth;

import lombok.Data;
import lombok.NonNull;

@Data
public class LoginRequest {
    @NonNull
    private String input;
    @NonNull
    private String password;
}
