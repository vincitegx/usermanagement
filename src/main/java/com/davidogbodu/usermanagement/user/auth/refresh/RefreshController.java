package com.davidogbodu.usermanagement.user.auth.refresh;

import com.davidogbodu.usermanagement.user.UserDto;
import com.davidogbodu.usermanagement.user.auth.JwtResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.security.auth.RefreshFailedException;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class RefreshController {
    private final RefreshTokenService refreshTokenService;
    private final HttpServletRequest request;
    private final WebClient userInfoClient;
    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret}")
    private String clientSecret;
    @PostMapping("refresh/token")
    @ResponseStatus(HttpStatus.OK)
    public JwtResponse refreshToken(@RequestBody UserDto user) throws RefreshFailedException {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh-token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if(refreshToken != null && !refreshToken.isEmpty()){
            if(refreshTokenService.existByToken(refreshToken)){
                return refreshTokenService.refreshToken(user, refreshToken);
            } else {
                SecurityContextHolder.clearContext();
                throw new RefreshFailedException("No refresh token found");
            }
        }else{
            SecurityContextHolder.clearContext();
            throw new RefreshFailedException("No refresh token found");
        }
    }
}
