package com.davidogbodu.usermanagement.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.davidogbodu.usermanagement.user.UserException;
import com.davidogbodu.usermanagement.user.Users;
import com.davidogbodu.usermanagement.user.auth.refresh.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import static java.util.Date.from;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

@Service
public class JWTUtil {
    private static final String SECRET_KEY =
            "foobar_123456789_foobar_123456789_foobar_123456789_foobar_123456789";

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id}")
    private String clientId;

    private Collection<? extends GrantedAuthority> getAuthorities(Users user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    public String generateJwtToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(180)))
                .withIssuer("https://davidtega.com")
                .withIssuedAt(Date.from(Instant.now()))
                .withClaim("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .sign(HMAC256(SECRET_KEY.getBytes()));
    }

    public String generateJwtToken(RefreshToken refreshToken) {
        try {
            User principal = (User) User.builder()
                    .username(refreshToken.getUser().getEmail())
                    .authorities(getAuthorities(refreshToken.getUser()))
                    .password(refreshToken.getUser().getPassword())
                    .disabled(refreshToken.getUser().getEnabled())
                    .accountExpired(false)
                    .accountLocked(refreshToken.getUser().getNonLocked())
                    .credentialsExpired(false)
                    .build();
            return JWT.create()
                    .withSubject(refreshToken.getUser().getEmail())
                    .withExpiresAt(Date.from(Instant.now().plusSeconds(180)))
                    .withIssuer("https://davidtega.com")
                    .withIssuedAt(from(Instant.now()))
                    .withClaim("roles", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .sign(HMAC256(SECRET_KEY.getBytes()));
        } catch (JWTCreationException | IllegalArgumentException ex) {
            throw new UserException(ex.getMessage());
        }
    }

    public String validateToken(String token) {
        Date now = from(Instant.now());
        Algorithm algorithm = HMAC256(SECRET_KEY.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).withIssuer("https://davidtega.com").build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            String email = decodedJWT.getSubject();
            if(email != null && decodedJWT.getExpiresAt().after(now)){
                return email;
            }else {
                return null;
            }
        }catch (JWTVerificationException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

}