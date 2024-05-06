package com.davidogbodu.usermanagement.user.registration;

import com.davidogbodu.usermanagement.user.UserService;
import com.davidogbodu.usermanagement.user.Users;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final PasswordValidator passwordValidator;
    private final UserService userService;
    private final RegistrationMapper registrationMapper;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResponse registerUser(RegistrationRequest registerRequest) {
        if(!passwordValidator.test(registerRequest.getPassword())){
            throw new RegistrationException("Password must be a minimum of eight characters contain at least one uppercase letter, one lowercase letter, one number and one special character");
        } else if (userService.findUserByEmailOrPhone(registerRequest.getEmail(), registerRequest.getPhoneNumber()).isPresent()) {
            throw new RegistrationException("Email is already registered");
        } else {
            registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            Users user = registrationMapper.mapRegistrationRequestToUser(registerRequest);
            user = userService.saveUser(user);
            return registrationMapper.mapUserToRegistrationResponse(user);
        }
    }
}
