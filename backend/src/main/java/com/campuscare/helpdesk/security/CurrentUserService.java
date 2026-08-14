package com.campuscare.helpdesk.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.campuscare.helpdesk.entity.AppUser;
import com.campuscare.helpdesk.entity.UserRole;
import com.campuscare.helpdesk.exception.ResourceNotFoundException;
import com.campuscare.helpdesk.repository.UserRepository;

@Component
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public boolean isAdmin(AppUser user) {
        return user.getRole() == UserRole.ADMIN;
    }
}
