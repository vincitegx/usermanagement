package com.davidogbodu.usermanagement.security;

import com.davidogbodu.usermanagement.user.UserRepository;
import com.davidogbodu.usermanagement.user.Users;
import com.davidogbodu.usermanagement.user.auth.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Users user = userRepository.findByEmailOrPhone(input,input).orElseThrow(()-> new UsernameNotFoundException("User not found with email "+ input));
        try {
            validateLoginAttempt(user);
        } catch (ExecutionException ex) {
            Logger.getLogger(UserDetailsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getEnabled(), true, true, user.getNonLocked(), getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Users user) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    private void validateLoginAttempt(Users user) throws ExecutionException {
        if(user.getNonLocked()) {
            if(loginAttemptService.hasExceededMaxAttempts(user.getEmail())) {
                user.setNonLocked(Boolean.FALSE);
            } else {
                user.setNonLocked(Boolean.TRUE);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }
}
