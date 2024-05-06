package com.davidogbodu.usermanagement.user.auth.refresh;

import com.davidogbodu.usermanagement.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByTokenAndUser(String token, Users user);

    void deleteByToken(String token);

}
