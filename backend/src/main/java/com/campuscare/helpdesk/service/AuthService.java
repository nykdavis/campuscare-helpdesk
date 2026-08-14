package com.campuscare.helpdesk.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campuscare.helpdesk.dto.AuthResponse;
import com.campuscare.helpdesk.dto.LoginRequest;
import com.campuscare.helpdesk.dto.RegisterRequest;
import com.campuscare.helpdesk.dto.UserResponse;
import com.campuscare.helpdesk.entity.AppUser;
import com.campuscare.helpdesk.entity.UserRole;
import com.campuscare.helpdesk.exception.ConflictException;
import com.campuscare.helpdesk.exception.InvalidCredentialsException;
import com.campuscare.helpdesk.repository.UserRepository;
import com.campuscare.helpdesk.security.JwtService;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account already exists for this email");
        }
        AppUser user = userRepository.save(new AppUser(request.name().trim(), email,
                passwordEncoder.encode(request.password()), UserRole.STUDENT));
        return response(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return response(user);
    }

    private AuthResponse response(AppUser user) {
        return new AuthResponse(jwtService.createToken(user), "Bearer", jwtService.expirationSeconds(),
                new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }
}
