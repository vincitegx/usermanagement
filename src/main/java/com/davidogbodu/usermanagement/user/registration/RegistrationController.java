package com.davidogbodu.usermanagement.user.registration;

import com.davidogbodu.usermanagement.mail.EventDto;
import com.davidogbodu.usermanagement.mail.MailService;
import com.davidogbodu.usermanagement.user.verification.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final VerificationService verificationService;
    private final MailService mailService;

    @PostMapping("register/user")
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody RegistrationRequest registerRequest) {
        RegistrationResponse response = registrationService.registerUser(registerRequest);
        EventDto eventDto = verificationService.registerVerificationTokenToDb(response);
        mailService.sendHtmlEmail(eventDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}