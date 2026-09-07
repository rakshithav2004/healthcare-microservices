package com.healthcare.auth.service;

import com.healthcare.auth.dto.AuthResponse;
import com.healthcare.auth.dto.LoginRequest;
import com.healthcare.auth.dto.RegisterRequest;
import com.healthcare.auth.model.Role;
import com.healthcare.auth.model.User;
import com.healthcare.auth.repository.UserRepository;
import com.healthcare.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id("user-123")
                .email("patient@example.com")
                .password("encoded-password")
                .role(Role.PATIENT)
                .build();
    }

    @Test
    void register_shouldCreateUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("patient@example.com");
        request.setPassword("password123");
        request.setRole(Role.PATIENT);

        when(userRepository.existsByEmail("patient@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        AuthResponse response =
                authService.register(request);

        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("patient@example.com", response.getEmail());
        assertEquals("PATIENT", response.getRole());

        verify(userRepository)
                .existsByEmail("patient@example.com");

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("patient@example.com");
        request.setPassword("password123");
        request.setRole(Role.PATIENT);

        when(userRepository.existsByEmail("patient@example.com"))
                .thenReturn(true);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authService.register(request)
                );

        assertEquals(
                "User already exists with email: patient@example.com",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail("patient@example.com");

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void register_shouldEncodePasswordBeforeSaving() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password123");
        request.setRole(Role.DOCTOR);

        User doctor = User.builder()
                .id("doctor-123")
                .email("doctor@example.com")
                .password("encoded-password")
                .role(Role.DOCTOR)
                .build();

        when(userRepository.existsByEmail("doctor@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(doctor);

        AuthResponse response =
                authService.register(request);

        assertEquals("doctor-123", response.getUserId());
        assertEquals("DOCTOR", response.getRole());

        verify(passwordEncoder)
                .encode("password123");

        verify(userRepository).save(
                argThat(savedUser ->
                        savedUser.getPassword()
                                .equals("encoded-password")
                )
        );
    }

    @Test
    void login_shouldReturnTokenForValidCredentials() {

        LoginRequest request = new LoginRequest();
        request.setEmail("patient@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("patient@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        )).thenReturn(true);

        when(jwtService.generateToken(
                "user-123",
                "patient@example.com",
                "PATIENT"
        )).thenReturn("jwt-token");

        AuthResponse response =
                authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("user-123", response.getUserId());
        assertEquals("patient@example.com", response.getEmail());
        assertEquals("PATIENT", response.getRole());

        verify(userRepository)
                .findByEmail("patient@example.com");

        verify(passwordEncoder)
                .matches("password123", "encoded-password");

        verify(jwtService)
                .generateToken(
                        "user-123",
                        "patient@example.com",
                        "PATIENT"
                );
    }

    @Test
    void login_shouldThrowExceptionWhenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@example.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordIsInvalid() {

        LoginRequest request = new LoginRequest();
        request.setEmail("patient@example.com");
        request.setPassword("wrong-password");

        when(userRepository.findByEmail("patient@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        )).thenReturn(false);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(passwordEncoder)
                .matches(
                        "wrong-password",
                        "encoded-password"
                );

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        anyString(),
                        anyString()
                );
    }
}