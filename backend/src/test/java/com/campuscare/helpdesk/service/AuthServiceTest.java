package com.campuscare.helpdesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.campuscare.helpdesk.dto.AuthResponse;
import com.campuscare.helpdesk.dto.LoginRequest;
import com.campuscare.helpdesk.dto.RegisterRequest;
import com.campuscare.helpdesk.entity.AppUser;
import com.campuscare.helpdesk.entity.UserRole;
import com.campuscare.helpdesk.exception.ConflictException;
import com.campuscare.helpdesk.exception.InvalidCredentialsException;
import com.campuscare.helpdesk.repository.UserRepository;
import com.campuscare.helpdesk.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    @Test
    void registrationCreatesStudentAndReturnsToken() {
        when(userRepository.existsByEmailIgnoreCase("asha@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.createToken(any(AppUser.class))).thenReturn("signed-token");
        when(jwtService.expirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.register(new RegisterRequest("Asha", "ASHA@example.com", "password123"));

        assertEquals("signed-token", response.accessToken());
        assertEquals(UserRole.STUDENT, response.user().role());
        assertEquals("asha@example.com", response.user().email());
    }

    @Test
    void duplicateRegistrationIsRejected() {
        when(userRepository.existsByEmailIgnoreCase("asha@example.com")).thenReturn(true);
        assertThrows(ConflictException.class,
                () -> authService.register(new RegisterRequest("Asha", "asha@example.com", "password123")));
    }

    @Test
    void wrongPasswordDoesNotRevealWhichCredentialFailed() {
        AppUser user = new AppUser("Asha", "asha@example.com", "encoded", UserRole.STUDENT);
        when(userRepository.findByEmailIgnoreCase("asha@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "encoded")).thenReturn(false);
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("asha@example.com", "wrong-pass")));
        assertEquals("Invalid email or password", exception.getMessage());
    }
}
