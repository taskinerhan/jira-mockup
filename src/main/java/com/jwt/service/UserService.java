package com.jwt.service;

import com.jwt.Entity.User;
import com.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userRepository;
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return userRepository.findByEmail(currentUserName)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }
}
