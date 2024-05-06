package com.davidogbodu.usermanagement.user.auth;

import com.davidogbodu.usermanagement.security.JWTUtil;
import com.davidogbodu.usermanagement.user.UserDto;
import com.davidogbodu.usermanagement.user.UserMapper;
import com.davidogbodu.usermanagement.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final UserMapper userDtoMapper;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    public JwtResponse login(LoginRequest request) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getInput(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateJwtToken(authentication);
        UserDto userDto = userDtoMapper.apply(userService.getCurrentUser());
        return new JwtResponse(token,userDto);
    }
}