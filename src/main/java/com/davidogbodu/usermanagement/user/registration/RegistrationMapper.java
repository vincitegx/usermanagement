package com.davidogbodu.usermanagement.user.registration;

import com.davidogbodu.usermanagement.user.Roles;
import com.davidogbodu.usermanagement.user.Users;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class RegistrationMapper {
    public Users mapRegistrationRequestToUser(RegistrationRequest registrationRequest) {
        ZonedDateTime createdAt = ZonedDateTime.now();
        return Users.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .phone(registrationRequest.getPhoneNumber())
                .password(registrationRequest.getPassword())
                .enabled(false)
                .nonLocked(false)
                .role(Roles.USER.name())
                .createdAt(createdAt)
                .build();
    }

    public RegistrationResponse mapUserToRegistrationResponse(Users users) {
        return RegistrationResponse.builder()
                .userName(users.getFirstName()+" "+users.getLastName())
                .userId(users.getId())
                .phoneNumber(users.getPhone())
                .email(users.getEmail())
                .build();
    }
}