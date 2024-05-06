package com.davidogbodu.usermanagement.user.registration;

import lombok.Data;
import lombok.NonNull;

@Data
public class RegistrationRequest {

    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String phoneNumber;
    @NonNull
    private String email;
    @NonNull
    private String password;
}
