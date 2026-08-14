package com.campuscare.helpdesk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.campuscare.helpdesk.entity.AppUser;
import com.campuscare.helpdesk.entity.UserRole;
import com.campuscare.helpdesk.repository.UserRepository;

@Component
public class AdminAccountInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminName;
    private final String adminEmail;
    private final String adminPassword;

    public AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
            @Value("${campuscare.admin.name:CampusCare Admin}") String adminName,
            @Value("${campuscare.admin.email:}") String adminEmail,
            @Value("${campuscare.admin.password:}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() && adminPassword.isBlank()) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.length() < 8) {
            throw new IllegalStateException("Admin email and a password of at least 8 characters must both be set");
        }
        if (!userRepository.existsByEmailIgnoreCase(adminEmail)) {
            userRepository.save(new AppUser(adminName, adminEmail,
                    passwordEncoder.encode(adminPassword), UserRole.ADMIN));
        }
    }
}
