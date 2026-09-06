package com.healthcare.auth.service;

import com.healthcare.auth.dto.AuthResponse;
import com.healthcare.auth.dto.LoginRequest;
import com.healthcare.auth.dto.RegisterRequest;
import com.healthcare.auth.model.User;
import com.healthcare.auth.repository.UserRepository;
import com.healthcare.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        log.info(
                "Registration attempt: email={}, role={}",
                request.getEmail(),
                request.getRole()
        );

        if (userRepository.existsByEmail(request.getEmail())) {

            log.warn(
                    "Registration failed: user already exists, email={}",
                    request.getEmail()
            );

            throw new IllegalStateException(
                    "User already exists with email: " + request.getEmail()
            );
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        log.info(
                "User registered successfully: userId={}, role={}",
                savedUser.getId(),
                savedUser.getRole()
        );

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        log.info(
                "Login attempt: email={}",
                request.getEmail()
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {

                    log.warn(
                            "Login failed: user not found, email={}",
                            request.getEmail()
                    );

                    return new IllegalStateException(
                            "Invalid email or password"
                    );
                });

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            log.warn(
                    "Login failed: invalid password, email={}",
                    request.getEmail()
            );

            throw new IllegalStateException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        log.info(
                "Login successful: userId={}, role={}",
                user.getId(),
                user.getRole()
        );

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}