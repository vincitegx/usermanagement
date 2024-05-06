package com.davidogbodu.usermanagement.user.verification;

import com.davidogbodu.usermanagement.mail.EventDto;
import com.davidogbodu.usermanagement.user.UserException;
import com.davidogbodu.usermanagement.user.UserService;
import com.davidogbodu.usermanagement.user.Users;
import com.davidogbodu.usermanagement.user.registration.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationService {

    @Value("${activation.token.expiration.time.hours}")
    private Long activationTokenExpirationTimeInHours;

    @Value("${organization.properties.mail}")
    private String organizationEmail;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserService userService;
    private final Clock clock = Clock.systemUTC();

    public EventDto registerVerificationTokenToDb(RegistrationResponse registrationResponse) {
        String generatedToken = UUID.randomUUID().toString();
        Users user = userService.findById(registrationResponse.getUserId()).orElseThrow(()-> new UsernameNotFoundException("No such user found"));
        emailVerificationTokenRepository.findByUser(user).ifPresent(emailVerificationTokenRepository::delete);
        EmailVerificationToken verificationToken = new EmailVerificationToken(generatedToken, user, LocalDateTime.now().plusHours(activationTokenExpirationTimeInHours));
        emailVerificationTokenRepository.save(verificationToken);
        Map<String, String> data = new HashMap<>();
        data.put("subject", "Email verification");
        data.put("name", user.getPhone());
        data.put("token", generatedToken);
        data.put("expiresAt", activationTokenExpirationTimeInHours.toString());
        return EventDto.builder().from(organizationEmail).to(registrationResponse.getEmail()).data(data).build();
    }

    public void requestNewVerificationToken(String input) {
        Users user = userService.findUserByEmailOrPhone(input,input).orElseThrow(() -> new UsernameNotFoundException("This email has not been registered. Visit the registration page to register an account..."));
        RegistrationResponse response = new RegistrationResponse(user.getId(), user.getPhone(), user.getEmail(), user.getFirstName()+" "+user.getLastName());
        registerVerificationTokenToDb(response);
    }

    public Users verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token).orElseThrow(() -> new UserException("Token does not exist !!!"));
        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
            throw new UserException("Token Expired !!!");
        } else {
            Users user = verificationToken.getUser();
            user.setEnabled(true);
            user.setNonLocked(true);
            user = userService.saveUser(user);
            emailVerificationTokenRepository.delete(verificationToken);
            return user;
        }
    }
}
