package com.davidogbodu.usermanagement.user.auth.refresh;

import com.davidogbodu.usermanagement.security.JWTUtil;
import com.davidogbodu.usermanagement.user.UserDto;
import com.davidogbodu.usermanagement.user.UserException;
import com.davidogbodu.usermanagement.user.UserMapper;
import com.davidogbodu.usermanagement.user.Users;
import com.davidogbodu.usermanagement.user.auth.JwtResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.RefreshFailedException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final UserMapper userMapper;
    private final Clock clock = Clock.systemDefaultZone();

    public String generateRefreshToken(Users user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        refreshToken.setUser(user);
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public JwtResponse refreshToken(UserDto user, String token) throws RefreshFailedException {
        RefreshToken refreshToken = validateRefreshToken(user, token);
        String authToken = jwtUtil.generateJwtToken(refreshToken);
        return new JwtResponse(authToken, user);
    }

    public RefreshToken validateRefreshToken(UserDto userDto, String token) throws RefreshFailedException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException("Invalid refresh Token"));
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now(clock))) {
            deleteRefreshToken(refreshToken.getToken());
            throw new RefreshFailedException("Refresh token was expired. Please make a new signin request");
        }
        UserDto user = userMapper.apply(refreshToken.getUser());
        if (!user.getId().equals(userDto.getId())) {
            throw new RefreshFailedException("You will need to login again");
        }
        return refreshToken;
    }

    public boolean existByToken(String token){
        return refreshTokenRepository.findByToken(token).isPresent();
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}
