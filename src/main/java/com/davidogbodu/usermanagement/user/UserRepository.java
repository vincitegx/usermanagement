package com.davidogbodu.usermanagement.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmailOrPhone(String email, String phone);

    Page<Users> findByRole(String name, Pageable pageable);
}
