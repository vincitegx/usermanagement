package com.davidogbodu.usermanagement.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<Users> findUserByEmailOrPhone(String email, String phoneNumber){
        return userRepository.findByEmailOrPhone(email,phoneNumber);
    }

    public Users saveUser(Users user){
        return userRepository.save(user);
    }

    public Optional<Users> findById(Long id) {
        return userRepository.findById(id);
    }

    public Page<Users> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true, rollbackFor = UsernameNotFoundException.class)
    public Users getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmailOrPhone(principal.getUsername(), principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + principal.getUsername()));
    }

    public Page<Users> findAllByRoleUser(Pageable pageable) {
        return userRepository.findByRole(Roles.USER.name(),pageable);
    }

    public String fetchAndEnableUser(Long userId){
        Users user = findById(userId).orElseThrow(()-> new UsernameNotFoundException("No user found with this id"));
        user.setEnabled(true);
        user.setNonLocked(true);
        user = saveUser(user);
        if(user.getEnabled() && user.getNonLocked()){
            return "User account has been activated !!!";
        }else{
            return "Sorry user account could not be activated, Please try again later :(";
        }
    }

    public String fetchAndDisableUser(Long userId){
        Users user = findById(userId).orElseThrow(()-> new UsernameNotFoundException("No user found with this id"));
        if(user.getRole().equals("SUPER_ADMIN")){
            throw new UserException("You cannot block or disable this account");
        }
        user.setEnabled(false);
        user.setNonLocked(false);
        saveUser(user);
        return "User account has been disabled !!!";
    }
}