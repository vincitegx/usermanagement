package com.davidogbodu.usermanagement.user.auth;

import com.davidogbodu.usermanagement.user.UserDto;
import com.davidogbodu.usermanagement.user.UserService;
import com.davidogbodu.usermanagement.user.Users;
import com.davidogbodu.usermanagement.user.auth.refresh.RefreshToken;
import com.davidogbodu.usermanagement.user.auth.refresh.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginService loginService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final HttpServletRequest request;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> userLogin(@RequestBody LoginRequest loginRequest, HttpServletResponse servletResponse) {
        JwtResponse response = loginService.login(loginRequest);
        Users user = userService.getCurrentUser();
        String refreshToken = refreshTokenService.generateRefreshToken(user);
        Cookie refreshTokenCookie = new Cookie("refresh-token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        refreshTokenCookie.setMaxAge((int) Duration.of(1, ChronoUnit.DAYS).toSeconds());
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("Priority", "High");
        servletResponse.addCookie(refreshTokenCookie);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, response.getAuthToken())
                .body(response);
    }


    @PostMapping("logout")
    public ResponseEntity<Boolean> logout(@RequestBody UserDto user, HttpServletResponse servletResponse) {
        try{
            Optional<Cookie> cookies = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                    .filter(cookie -> "refresh-token".equals(cookie.getName()))
                    .findFirst();
            if(cookies.isPresent()){
                RefreshToken refreshToken = refreshTokenService.validateRefreshToken(user, cookies.get().getValue());
                if(refreshToken != null){
                    refreshTokenService.deleteRefreshToken(refreshToken.getToken());
                }
                Cookie refreshTokenCookie = new Cookie("refresh-token", cookies.get().getValue());
                refreshTokenCookie.setHttpOnly(true);
                refreshTokenCookie.setSecure(true);
                refreshTokenCookie.setAttribute("SameSite", "None");
                refreshTokenCookie.setMaxAge(0);
                refreshTokenCookie.setPath("/");
                refreshTokenCookie.setAttribute("Priority", "High");
                servletResponse.addCookie(refreshTokenCookie);
            }else {
                SecurityContextHolder.clearContext();
            }
        }catch (Exception ex){
            SecurityContextHolder.clearContext();
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
