package com.davidogbodu.usermanagement.user.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegistrationResponse {
    private Long userId;
    private String phoneNumber;
    private String email;
    private String userName;
}